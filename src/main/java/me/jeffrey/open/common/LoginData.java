package me.jeffrey.open.common;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class LoginData {
    
    private String uname;
    private String password;
    private String captcha;
}
