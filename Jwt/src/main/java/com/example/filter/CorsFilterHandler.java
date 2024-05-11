package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Const.ORDER_CORS)
public class CorsFilterHandler extends HttpFilter {
    //这个方法是用来处理跨域请求的

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        addCorsHeader(response);
        chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");//允许所有的请求来源
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");//允许所有的请求方法
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");//允许所有的请求头
    }
}
