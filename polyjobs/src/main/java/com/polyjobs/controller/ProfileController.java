package com.polyjobs.controller;

import com.polyjobs.entity.Resume;
import com.polyjobs.entity.User;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        // Cập nhật lại thông tin mới nhất từ DB
        User user = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        session.setAttribute("loggedInUser", user);
        model.addAttribute("user", user);

        // Nếu là ứng viên, lấy danh sách CV
        if (Boolean.FALSE.equals(user.getRole())) {
            List<Resume> resumes = resumeRepository.findByCandidateOrderByUploadDateDesc(user);
            model.addAttribute("resumes", resumes);
        }

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("fullname") String fullname,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "profession", required = false) String profession,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loggedInUser.getId()).orElse(null);
        if (user != null) {
            user.setFullname(fullname);
            user.setPhone(phone);
            user.setAddress(address);
            if (profession != null) {
                user.setProfession(profession);
            }
            userRepository.save(user);
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/upload-cv")
    public String uploadCv(
            @RequestParam("cvFile") MultipartFile cvFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (cvFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CV!");
            return "redirect:/profile";
        }

        try {
            // Tạo thư mục nếu chưa có
            String uploadDir = "src/main/resources/static/uploads/cv/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Đặt tên file ngẫu nhiên để tránh trùng lặp
            String originalFilename = cvFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(uploadDir + newFilename);

            // Lưu file
            Files.copy(cvFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Lưu thông tin vào CSDL
            Resume resume = new Resume();
            resume.setCandidate(loggedInUser);
            resume.setFileName("/uploads/cv/" + newFilename);
            resume.setTitle(originalFilename);
            resume.setUploadDate(new Date());
            resumeRepository.save(resume);

            redirectAttributes.addFlashAttribute("success", "Tải lên CV thành công!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tải lên CV!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(
            @RequestParam("avatarFile") MultipartFile avatarFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (avatarFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ảnh!");
            return "redirect:/profile";
        }

        try {
            // Tạo thư mục nếu chưa có
            String uploadDir = "src/main/resources/static/uploads/avatars/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Đặt tên file ngẫu nhiên để tránh trùng lặp
            String originalFilename = avatarFile.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(uploadDir + newFilename);

            // Lưu file
            Files.copy(avatarFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Cập nhật URL ảnh đại diện vào User
            User user = userRepository.findById(loggedInUser.getId()).orElse(null);
            if (user != null) {
                user.setAvatar("/uploads/avatars/" + newFilename);
                userRepository.save(user);
                session.setAttribute("loggedInUser", user);
            }

            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tải lên ảnh!");
        }

        return "redirect:/profile";
    }
}
