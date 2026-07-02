package com.polyjobs.controller;

import com.polyjobs.entity.Company;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    // ═══ ĐĂNG NHẬP ═══

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            // Lưu thông tin user vào session
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/login";
        }
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

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã được sử dụng!");
            return "redirect:/register";
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
            return "redirect:/register";
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role); // false: Ứng viên | true: Nhà tuyển dụng

        user = userRepository.save(user);

        // Nếu đăng ký với vai trò Nhà tuyển dụng, tự động tạo hồ sơ Công ty
        if (Boolean.TRUE.equals(role) && companyName != null && !companyName.trim().isEmpty()) {
            Company company = new Company();
            company.setCompanyName(companyName.trim());
            company.setAddress(companyAddress != null ? companyAddress.trim() : "Đang cập nhật");
            company.setWebsite(companyWebsite != null ? companyWebsite.trim() : "");
            company.setDescription("Doanh nghiệp " + companyName.trim() + " hoạt động tại miền Tây Nam Bộ.");
            company.setEmployer(user);
            companyRepository.save(company);
        }

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }

    // ═══ ĐĂNG XUẤT ═══

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
