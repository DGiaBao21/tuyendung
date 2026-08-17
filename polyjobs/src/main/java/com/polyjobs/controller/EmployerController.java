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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private com.polyjobs.service.UserService userService;

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

    // Helper: Check if logged-in user is an Employer
    private boolean isEmployer(HttpSession session) {
        com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        return dto != null && Boolean.TRUE.equals(dto.getRole());
    }

    private User getEmployer(HttpSession session) {
        com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        return dto != null ? userService.findEntityById(dto.getId()) : null;
    }

    // --- Quản lý tin tuyển dụng ---
    @GetMapping("/jobs")
    public String myJobs(HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = getEmployer(session);
        List<Job> jobs = (employer != null) ? jobRepository.findByEmployer(employer) : new java.util.ArrayList<>();
        model.addAttribute("jobs", jobs);

        // Đếm số lượng ứng viên theo từng Job
        Map<Integer, Long> applicationCountMap = new HashMap<>();
        Map<Integer, Long> pendingCountMap = new HashMap<>();
        for (Job j : jobs) {
            List<Application> apps = applicationRepository.findByJob(j);
            applicationCountMap.put(j.getId(), (long) (apps != null ? apps.size() : 0));
            long pending = (apps != null) ? apps.stream().filter(a -> "Chờ duyệt".equals(a.getStatus())).count() : 0L;
            pendingCountMap.put(j.getId(), pending);
        }
        model.addAttribute("applicationCountMap", applicationCountMap);
        model.addAttribute("pendingCountMap", pendingCountMap);
        model.addAttribute("appCounts", applicationCountMap);

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
    public String saveJob(@ModelAttribute Job job, @RequestParam("categoryId") Integer categoryId,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = getEmployer(session);
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

        User employer = getEmployer(session);
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
    public String toggleJobStatus(@RequestParam("id") Integer id, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = getEmployer(session);
        Job job = jobRepository.findById(id).orElse(null);

        if (job != null && job.getEmployer().getId().equals(employer.getId())) {
            job.setStatus(!job.getStatus());
            jobRepository.save(job);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        }

        return "redirect:/employer/jobs";
    }

    // --- Quản lý ứng viên theo tin ---
    @GetMapping("/applications/{jobId}")
    public String manageApplications(@PathVariable("jobId") Integer jobId, HttpSession session, Model model) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = getEmployer(session);
        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null || !job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }

        List<Application> applications = applicationRepository.findByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        // Thống kê nhanh
        long totalCount = applications.size();
        long pendingCount = applications.stream().filter(a -> "Chờ duyệt".equals(a.getStatus())).count();
        long interviewCount = applications.stream().filter(a -> "Hẹn phỏng vấn".equals(a.getStatus())).count();
        long approvedCount = applications.stream().filter(a -> "Trúng tuyển".equals(a.getStatus())).count();
        long rejectedCount = applications.stream().filter(a -> "Từ chối".equals(a.getStatus())).count();
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "employer/applications";
    }

    @PostMapping("/application/status")
    public String updateApplicationStatus(@RequestParam("applicationId") Integer applicationId,
                                          @RequestParam("status") String status,
                                          @RequestParam(value = "note", required = false) String note,
                                          @RequestParam(value = "interviewDateStr", required = false) String interviewDateStr,
                                          @RequestParam(value = "interviewLocation", required = false) String interviewLocation,
                                          HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployer(session)) return "redirect:/login";

        User employer = getEmployer(session);
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application != null && application.getJob().getEmployer().getId().equals(employer.getId())) {
            application.setStatus(status);
            if (note != null) {
                application.setNote(note.trim());
            }

            // Xử lý ngày giờ phỏng vấn nếu có
            Date interviewDate = null;
            if (interviewDateStr != null && !interviewDateStr.trim().isEmpty()) {
                try {
                    if (interviewDateStr.trim().length() == 16) {
                        interviewDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(interviewDateStr.trim());
                    } else if (interviewDateStr.trim().length() >= 19) {
                        interviewDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(interviewDateStr.trim());
                    }
                } catch (Exception ignored) {}
            }

            if ("Hẹn phỏng vấn".equals(status)) {
                if (interviewDate != null) {
                    application.setInterviewDate(interviewDate);
                }
                if (interviewLocation != null && !interviewLocation.trim().isEmpty()) {
                    application.setInterviewLocation(interviewLocation.trim());
                }
            }

            applicationRepository.save(application);

            String formattedInterviewDate = "";
            if (application.getInterviewDate() != null) {
                formattedInterviewDate = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy").format(application.getInterviewDate());
            }

            // Gửi thông báo Web
            Notification notification = new Notification();
            notification.setUser(application.getCandidate());

            if ("Hẹn phỏng vấn".equals(status)) {
                notification.setTitle("📅 Lời mời phỏng vấn: " + application.getJob().getTitle());
                StringBuilder notifContent = new StringBuilder();
                notifContent.append("Công ty ").append(application.getJob().getCompany().getCompanyName())
                            .append(" đã gửi lời mời phỏng vấn cho vị trí [").append(application.getJob().getTitle()).append("].");
                if (!formattedInterviewDate.isEmpty()) {
                    notifContent.append("\n⏰ Thời gian: ").append(formattedInterviewDate);
                }
                if (application.getInterviewLocation() != null && !application.getInterviewLocation().isEmpty()) {
                    notifContent.append("\n📍 Địa điểm / Hình thức: ").append(application.getInterviewLocation());
                }
                if (note != null && !note.trim().isEmpty()) {
                    notifContent.append("\n📝 Lời nhắn từ HR: ").append(note.trim());
                }
                notification.setContent(notifContent.toString());
            } else {
                String emoji = "Trúng tuyển".equals(status) ? "🎉" : ("Từ chối".equals(status) ? "❌" : "🔔");
                notification.setTitle(emoji + " Kết quả ứng tuyển: " + application.getJob().getTitle());
                StringBuilder notifContent = new StringBuilder();
                notifContent.append("Hồ sơ ứng tuyển vị trí [").append(application.getJob().getTitle()).append("] tại ")
                            .append(application.getJob().getCompany().getCompanyName())
                            .append(" đã được cập nhật: ").append(status);
                if (note != null && !note.trim().isEmpty()) {
                    notifContent.append("\n📝 Lời nhắn từ HR: ").append(note.trim());
                }
                notification.setContent(notifContent.toString());
            }
            notificationRepository.save(notification);

            // Gửi thông báo Email
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Chào ").append(application.getCandidate().getFullname()).append(",\n\n");
            if ("Hẹn phỏng vấn".equals(status)) {
                emailContent.append("Công ty ").append(application.getJob().getCompany().getCompanyName())
                            .append(" trân trọng mời bạn tham gia buổi phỏng vấn cho vị trí: ")
                            .append(application.getJob().getTitle()).append(".\n\n");
                if (!formattedInterviewDate.isEmpty()) {
                    emailContent.append("⏰ Thời gian: ").append(formattedInterviewDate).append("\n");
                }
                if (application.getInterviewLocation() != null && !application.getInterviewLocation().isEmpty()) {
                    emailContent.append("📍 Địa điểm / Hình thức: ").append(application.getInterviewLocation()).append("\n");
                }
                if (note != null && !note.trim().isEmpty()) {
                    emailContent.append("📝 Lời nhắn từ Nhà tuyển dụng: ").append(note.trim()).append("\n");
                }
                emailContent.append("\nVui lòng chuẩn bị và tham gia đúng giờ. Chúc bạn có một buổi phỏng vấn thành công!\n\n");
            } else {
                emailContent.append("Trạng thái hồ sơ của bạn cho vị trí ").append(application.getJob().getTitle())
                            .append(" tại công ty ").append(application.getJob().getCompany().getCompanyName())
                            .append(" đã được cập nhật thành: ").append(status).append(".\n\n");
                if (note != null && !note.trim().isEmpty()) {
                    emailContent.append("Lời nhắn từ nhà tuyển dụng: ").append(note.trim()).append("\n\n");
                }
            }
            emailContent.append("Trân trọng,\nĐội ngũ Polyjobs");

            String emailSubject = "Hẹn phỏng vấn".equals(status) 
                    ? "Polyjobs - Lời mời phỏng vấn vị trí " + application.getJob().getTitle()
                    : "Polyjobs - Cập nhật trạng thái ứng tuyển";

            emailService.sendSimpleMessage(application.getCandidate().getEmail(), emailSubject, emailContent.toString());

            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái hồ sơ, gửi thông báo Web và Email tới ứng viên!");
            return "redirect:/employer/applications/" + application.getJob().getId();
        }

        return "redirect:/employer/jobs";
    }
}
