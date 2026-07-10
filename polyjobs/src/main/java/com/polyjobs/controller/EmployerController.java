package com.polyjobs.controller;

import com.polyjobs.entity.Company;
import com.polyjobs.entity.Job;
import com.polyjobs.entity.User;
import com.polyjobs.entity.Category;
import com.polyjobs.entity.Application;
import com.polyjobs.entity.Notification;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.CategoryRepository;
import com.polyjobs.repository.ApplicationRepository;
import com.polyjobs.repository.NotificationRepository;
import com.polyjobs.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    // Kiểm tra quyền Nhà tuyển dụng
    private boolean isEmployer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && Boolean.TRUE.equals(user.getRole());
    }

    // --- Quản lý tin tuyển dụng ---
    @GetMapping("/jobs")
    public String manageJobs(HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        List<Job> jobs = jobRepository.findByEmployer(employer);
        model.addAttribute("jobs", jobs);
        
        return "employer/jobs";
    }

    @GetMapping("/job/add")
    public String showAddJobForm(HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "employer/job-form";
    }

    @PostMapping("/job/save")
    public String saveJob(@ModelAttribute Job job, @RequestParam("categoryId") Integer categoryId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        Company company = companyRepository.findFirstByEmployer(employer);

        if (company == null || company.getCompanyName() == null || company.getCompanyName().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần cập nhật thông tin Công ty trong trang Cá nhân trước khi đăng tin!");
            return "redirect:/profile";
        }

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (job.getId() != null) {
            Job existingJob = jobRepository.findById(job.getId()).orElse(null);
            if (existingJob != null && existingJob.getEmployer().getId().equals(employer.getId())) {
                existingJob.setTitle(job.getTitle());
                existingJob.setDescription(job.getDescription());
                existingJob.setSalary(job.getSalary());
                existingJob.setLocation(job.getLocation());
                existingJob.setExperience(job.getExperience());
                existingJob.setWorkingType(job.getWorkingType());
                existingJob.setQuantity(job.getQuantity());
                existingJob.setBenefit(job.getBenefit());
                existingJob.setDeadline(job.getDeadline());
                existingJob.setStatus(job.getStatus());
                existingJob.setCategory(category);
                jobRepository.save(existingJob);
                redirectAttributes.addFlashAttribute("success", "Cập nhật tin tuyển dụng thành công!");
            }
        } else {
            job.setEmployer(employer);
            job.setCompany(company);
            job.setCategory(category);
            job.setCreatedDate(new Date());
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Đăng tin tuyển dụng thành công!");
        }

        return "redirect:/employer/jobs";
    }

    @GetMapping("/job/edit/{id}")
    public String showEditJobForm(@PathVariable("id") Integer id, HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        Job job = jobRepository.findById(id).orElse(null);

        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("job", job);
        
        return "employer/job-form";
    }

    @PostMapping("/job/toggle-status")
    public String toggleJobStatus(@RequestParam("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        Job job = jobRepository.findById(id).orElse(null);

        if (job != null && job.getEmployer().getId().equals(employer.getId())) {
            job.setStatus(!job.getStatus());
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        }

        return "redirect:/employer/jobs";
    }

    // --- Quản lý ứng viên ---
    @GetMapping("/applications/{jobId}")
    public String manageApplications(@PathVariable("jobId") Integer jobId, HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        List<Application> applications = applicationRepository.findByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        return "employer/applications";
    }

    @PostMapping("/application/status")
    public String updateApplicationStatus(@RequestParam("applicationId") Integer applicationId,
                                          @RequestParam("status") String status,
                                          @RequestParam(value = "note", required = false) String note,
                                          HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = (User) session.getAttribute("loggedInUser");
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application != null && application.getJob().getEmployer().getId().equals(employer.getId())) {
            application.setStatus(status);
            if (note != null) {
                application.setNote(note);
            }
            applicationRepository.save(application);

            // Gửi thông báo Web
            Notification notification = new Notification();
            notification.setUser(application.getCandidate());
            notification.setTitle("Cập nhật trạng thái ứng tuyển: " + application.getJob().getTitle());
            notification.setContent("Trạng thái hồ sơ của bạn cho vị trí " + application.getJob().getTitle() + " tại công ty " + application.getJob().getCompany().getCompanyName() + " đã được cập nhật thành: " + status + (note != null && !note.trim().isEmpty() ? ". Lời nhắn: " + note : ""));
            notificationRepository.save(notification);

            // Gửi thông báo Email
            String emailContent = "Chào " + application.getCandidate().getFullname() + ",\n\n"
                    + "Trạng thái hồ sơ của bạn cho vị trí " + application.getJob().getTitle() 
                    + " tại công ty " + application.getJob().getCompany().getCompanyName() 
                    + " đã được cập nhật thành: " + status + ".\n\n"
                    + (note != null && !note.trim().isEmpty() ? "Lời nhắn từ nhà tuyển dụng: " + note + "\n\n" : "")
                    + "Trân trọng,\nĐội ngũ Polyjobs";
            emailService.sendSimpleMessage(application.getCandidate().getEmail(), "Polyjobs - Cập nhật trạng thái ứng tuyển", emailContent);

            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái hồ sơ, gửi thông báo Web và Email tới ứng viên!");
            return "redirect:/employer/applications/" + application.getJob().getId();
        }

        return "redirect:/employer/jobs";
    }
}
