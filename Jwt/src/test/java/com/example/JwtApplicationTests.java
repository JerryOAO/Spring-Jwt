package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class JwtApplicationTests {

    @Test
    void contextLoads() {
        //这里是用来生成密码的 用于测试
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

}
