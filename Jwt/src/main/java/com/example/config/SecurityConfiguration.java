package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {
    @Resource   //Resource注解是用来标识这个属性是一个依赖注入的属性
    JwtUtils utils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;
    @Resource
    AccountService service;
    @Bean
    //这个方法是用来配置Security的过滤器链
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //这里是配置请求的匹配规则
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                //这里是配置登录的相关信息
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess)
                )
                //这里是配置登出的相关信息
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                //这里是配置异常处理
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                //csrf是一种防止跨站请求伪造的机制 这里是禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                //这里是配置session的管理策略
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //这里是配置JWT的过滤器
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    //这里是处理登录成功的方法
    private void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication)throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        //这一步是获取用户信息 authentication.getPrincipal() 返回的是一个Object对象,包含了用户的信息
        User user = (User) authentication.getPrincipal();
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        String token = utils.createJwt(user,account.getId(),account.getUsername());
        //这里是返回给前端的数据
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpire(utils.expireTime());
        vo.setToken(token);
        BeanUtils.copyProperties(account,vo);
//      vo.setUsername(account.getUsername());
//      vo.setRole(account.getRole());


        response.getWriter().write(RestBean.success(vo).asJsonString());
    }
    //这里是处理登出成功的方法
    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication)throws IOException, ServletException {
        // TODO 退出登录成功后的处理
        response.setContentType("application/json;charset=UTF-8");
        //这里是获取输出流 用来向前端返回数据
        PrintWriter writer = response.getWriter();
        //这里是获取请求头中的Authorization
        String authorization = request.getHeader("Authorization");
        //这里是判断token是否失效 如果失效就返回成功 如果没有失效就返回失败
        if(utils.invalidateJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }
        else {
            writer.write(RestBean.failure(500,"退出登录失败").asJsonString());
        }
    }
    //这里是处理登录失败的方法
    private void onAuthenticationFailure(HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException e)throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.unauthorized(e.getMessage()).asJsonString());
    }

    //这里是处理访问被拒绝的方法(权限不足的方法) 403
    private void onAccessDeny(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.forbidden(e.getMessage()).asJsonString());
    }

    //这里是处理未授权的方法
    private void onUnauthorized(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.unauthorized("未授权").asJsonString());
    }

}
