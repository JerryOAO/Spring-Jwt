package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    FlowUtils utils;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    PasswordEncoder encoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .authorities(account.getRole())
                .build();
    }
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        //笔记：限制频繁请求验证码 synchronized保证同一时刻只有一个线程进入
        synchronized (ip.intern()){
            if (!verifyLimit(ip)) return "请勿频繁请求验证码";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            //将数据发送到rabbitmq
            amqpTemplate.convertAndSend("mail", data);
            //将验证码存入redis 用于后续验证
            stringRedisTemplate.opsForValue().set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            //返回空字符串表示发送成功
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        //codec从redis中取  此处code为redis中的验证码
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误，请重新输入";
        if(existsAccountByEmail(email)) return "邮箱已被注册，请更换邮箱";
        if(existsAccountByUsername(username)) return "用户名已被注册，请更换用户名";
        //笔记：密码加密
        String password = encoder.encode(vo.getPassword());
        Account account = new Account(null,username,password,email, "user",new Date());
        if(this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    //判断邮箱是否已存在
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

    //笔记：限制频繁请求验证码
    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        //笔记：限制 30 秒内只能请求一次
        return utils.limitOnceCheck(key, 30);
    }
}
