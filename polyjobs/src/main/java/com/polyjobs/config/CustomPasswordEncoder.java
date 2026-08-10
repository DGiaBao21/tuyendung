package com.polyjobs.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        // Luôn luôn mã hóa bằng BCrypt khi lưu mới
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        
        // Kiểm tra xem mật khẩu trong DB có phải là BCrypt hash không (thường bắt đầu bằng $2a$, $2b$, $2y$)
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bcrypt.matches(rawPassword, encodedPassword);
        } else {
            // Nếu không phải BCrypt, so sánh plaintext (Dành cho tài khoản cũ)
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}
