package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    // Middleware check admin
    private boolean checkAdmin(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        return loggedInUser != null && loggedInUser.getIsAdmin() != null && loggedInUser.getIsAdmin();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalCandidates", userRepository.findByRole(false).size());
        model.addAttribute("totalEmployers", userRepository.findByRole(true).size());
        
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("totalCompanies", companyRepository.count());
        model.addAttribute("totalResumes", resumeRepository.count());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        return "admin/users";
    }

    @PostMapping("/users/toggle-block/{id}")
    public String toggleBlockUser(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) return "redirect:/login";

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Không cho phép tự block chính mình
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (user.getId().equals(loggedInUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Không thể tự chặn chính mình!");
                return "redirect:/admin/users";
            }

            user.setIsActive(user.getIsActive() == null || !user.getIsActive());
            userRepository.save(user);
            String status = user.getIsActive() ? "bỏ chặn" : "chặn";
            redirectAttributes.addFlashAttribute("success", "Đã " + status + " người dùng: " + user.getUsername());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String jobs(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        List<Job> jobs = jobRepository.findAll();
        model.addAttribute("jobs", jobs);

        return "admin/jobs";
    }

    @PostMapping("/jobs/toggle-hide/{id}")
    public String toggleHideJob(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) return "redirect:/login";

        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            job.setIsHidden(job.getIsHidden() == null || !job.getIsHidden());
            jobRepository.save(job);
            String status = job.getIsHidden() ? "ẩn" : "hiện";
            redirectAttributes.addFlashAttribute("success", "Đã " + status + " tin tuyển dụng: " + job.getTitle());
        }

        return "redirect:/admin/jobs";
    }
}
