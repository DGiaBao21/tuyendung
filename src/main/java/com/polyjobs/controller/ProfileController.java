package com.polyjobs.controller;

import com.polyjobs.entity.Resume;
import com.polyjobs.entity.User;
import com.polyjobs.entity.Application;
import com.polyjobs.entity.SavedJob;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.UserRepository;
import com.polyjobs.repository.ApplicationRepository;
import com.polyjobs.repository.SavedJobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private com.polyjobs.repository.CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private com.polyjobs.service.FileUploadService fileUploadService;

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

        // Nếu là ứng viên, lấy danh sách CV, lịch sử ứng tuyển, việc làm đã lưu
        if (Boolean.FALSE.equals(user.getRole())) {
            List<Resume> resumes = resumeRepository.findByCandidateOrderByUploadDateDesc(user);
            model.addAttribute("resumes", resumes);
            
            List<Application> applications = applicationRepository.findByCandidate(user);
            model.addAttribute("applications", applications);
            
            List<SavedJob> savedJobs = savedJobRepository.findByCandidate(user);
            model.addAttribute("savedJobs", savedJobs);
        } else if (Boolean.TRUE.equals(user.getRole())) {
            // Nếu là nhà tuyển dụng, lấy thông tin Công ty
            com.polyjobs.entity.Company company = companyRepository.findFirstByEmployer(user);
            if (company == null) {
                company = new com.polyjobs.entity.Company();
                company.setCompanyName("Chưa cập nhật tên công ty");
                company.setEmployer(user);
                companyRepository.save(company);
            }
            model.addAttribute("company", company);
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

    @PostMapping("/profile/update-company")
    public String updateCompany(
            @RequestParam("companyName") String companyName,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getRole())) {
            return "redirect:/login";
        }

        com.polyjobs.entity.Company company = companyRepository.findFirstByEmployer(loggedInUser);
        if (company != null) {
            company.setCompanyName(companyName);
            company.setWebsite(website);
            company.setAddress(address);
            company.setDescription(description);

            if (logoFile != null && !logoFile.isEmpty()) {
                try {
                    String logoUrl = fileUploadService.saveFile(logoFile, "company");
                    if (logoUrl != null) {
                        company.setLogo(logoUrl);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            companyRepository.save(company);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin công ty thành công!");
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
            String cvUrl = fileUploadService.saveFile(cvFile, "cv");
            if (cvUrl != null) {
                String originalFilename = cvFile.getOriginalFilename();
                Resume resume = new Resume();
                resume.setCandidate(loggedInUser);
                resume.setFileName(cvUrl);
                resume.setTitle(originalFilename);
                resume.setUploadDate(new Date());
                resumeRepository.save(resume);

                redirectAttributes.addFlashAttribute("success", "Tải lên CV thành công!");
            }
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
            String avatarUrl = fileUploadService.saveFile(avatarFile, "avatars");
            if (avatarUrl != null) {
                User user = userRepository.findById(loggedInUser.getId()).orElse(null);
                if (user != null) {
                    user.setAvatar(avatarUrl);
                    userRepository.save(user);
                    session.setAttribute("loggedInUser", user);
                }
                redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tải lên ảnh!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/delete-cv/{id}")
    public String deleteCv(
            @PathVariable("id") Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Resume resume = resumeRepository.findById(id).orElse(null);
        if (resume != null && resume.getCandidate().getId().equals(loggedInUser.getId())) {
            try {
                fileUploadService.deleteFile(resume.getFileName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            resumeRepository.delete(resume);
            redirectAttributes.addFlashAttribute("success", "Đã xóa CV thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa CV này!");
        }

        return "redirect:/profile";
    }
}
