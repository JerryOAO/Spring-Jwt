package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z0-9_]{1,10}$")
    @Length(min = 1, max = 10)
    String username;
    @Length(min = 6, max = 20)
    String password;

}
