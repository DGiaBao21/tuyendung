package com.polyjobs.controller;

import com.polyjobs.entity.Company;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.dto.RegisterDTO;
import com.polyjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ═══ ĐĂNG NHẬP (Spring Security xử lý POST /login) ═══

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("success", "Đăng xuất thành công!");
        }
        return "login";
    }

    // ═══ ĐĂNG KÝ ═══

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "role", required = false) String role, Model model) {
        model.addAttribute("initialRole", "employer".equalsIgnoreCase(role));
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "role", defaultValue = "false") Boolean role,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "companyAddress", required = false) String companyAddress,
            @RequestParam(value = "companyWebsite", required = false) String companyWebsite,
            RedirectAttributes redirectAttributes) {

        // Trim inputs
        username = username != null ? username.trim() : "";
        password = password != null ? password.trim() : "";
        email = email != null ? email.trim() : "";

        // Kiểm tra username đã tồn tại
        if (userService.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã được sử dụng!");
            return "redirect:/register";
        }

        // Kiểm tra email đã tồn tại
        if (userService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
            return "redirect:/register";
        }

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword(password);
        registerDTO.setFullname(fullname);
        registerDTO.setEmail(email);
        registerDTO.setPhone(phone);
        registerDTO.setRole(role);
        registerDTO.setCompanyName(companyName);
        registerDTO.setCompanyAddress(companyAddress);
        registerDTO.setCompanyWebsite(companyWebsite);

        userService.registerUser(registerDTO);

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }

    
}
