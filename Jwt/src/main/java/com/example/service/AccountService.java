package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends IService<Account>, UserDetailsService {
    //笔记：根据用户名或邮箱查找用户
    Account findAccountByNameOrEmail(String text);
    //笔记：注册邮箱验证码
    String registerEmailVerifyCode(String type,String email,String ip);
    //笔记：注册邮件账户
    String registerEmailAccount(EmailRegisterVO vo);
}
