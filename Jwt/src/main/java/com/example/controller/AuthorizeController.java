package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@Validated // 开启邮箱参数校验
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthorizeController {
    @Resource
    AccountService service;

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp="(register|reset)") String type,
                                        HttpServletRequest request) {
        return messageHandle(() -> service.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo) {
        return messageHandle(() -> service.registerEmailAccount(vo));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400,message);
    }
}
