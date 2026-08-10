package com.polyjobs.config;

import com.polyjobs.dto.UserDTO;
import com.polyjobs.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        UserDTO userDTO = userService.findByUsername(username);

        if (userDTO != null) {
            // Lưu UserDTO vào session để Thymeleaf template sử dụng (an toàn hơn)
            request.getSession().setAttribute("loggedInUser", userDTO);
            
            if (Boolean.TRUE.equals(userDTO.getIsAdmin())) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (Boolean.TRUE.equals(userDTO.getRole())) {
                response.sendRedirect("/employer/jobs");
                return;
            }
        }

        // Redirect về trang chủ sau khi đăng nhập thành công cho candidate
        response.sendRedirect("/");
    }
}
