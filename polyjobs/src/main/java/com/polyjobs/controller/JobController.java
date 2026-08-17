package com.polyjobs.controller;

import com.polyjobs.entity.Application;
import com.polyjobs.entity.Job;
import com.polyjobs.entity.Resume;
import com.polyjobs.entity.User;
import com.polyjobs.entity.SavedJob;
import com.polyjobs.repository.ApplicationRepository;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.SavedJobRepository;
import com.polyjobs.service.FileUploadService;
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

import java.util.Date;
import java.util.Optional;

@Controller
public class JobController {

    @Autowired
    private com.polyjobs.service.UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    // Trang chi tiết công việc
    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Optional<Job> jobOptional = jobRepository.findById(id);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            model.addAttribute("job", job);

            com.polyjobs.dto.UserDTO loggedInUserDTO = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
            User loggedInUser = loggedInUserDTO != null ? userService.findEntityById(loggedInUserDTO.getId()) : null;

            if (loggedInUser != null && Boolean.FALSE.equals(loggedInUser.getRole()) && !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
                // Kiểm tra xem đã ứng tuyển chưa
                boolean hasApplied = applicationRepository.findByJob(job).stream()
                        .anyMatch(a -> a.getCandidate().getId().equals(loggedInUser.getId()));
                model.addAttribute("hasApplied", hasApplied);

                // Kiểm tra xem đã lưu việc làm chưa
                boolean isSaved = savedJobRepository.findByCandidateAndJob(loggedInUser, job).isPresent();
                model.addAttribute("isSaved", isSaved);
            }

            return "job-detail";
        } else {
            return "redirect:/";
        }
    }

    // Ứng tuyển công việc — upload CV trực tiếp
    @PostMapping("/job/apply")
    public String applyJob(@RequestParam("jobId") Integer jobId,
                           @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                           @RequestParam(value = "coverLetter", required = false) String coverLetter,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        com.polyjobs.dto.UserDTO loggedInUserDTO = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        User loggedInUser = loggedInUserDTO != null ? userService.findEntityById(loggedInUserDTO.getId()) : null;

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để ứng tuyển.");
            return "redirect:/login";
        }

        if (Boolean.TRUE.equals(loggedInUser.getRole()) || Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "Nhà tuyển dụng hoặc quản trị viên không thể ứng tuyển.");
            return "redirect:/job/" + jobId;
        }

        if (cvFile == null || cvFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CV để ứng tuyển!");
            return "redirect:/job/" + jobId;
        }

        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (!jobOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy công việc!");
            return "redirect:/job/" + jobId;
        }

        try {
            // Upload CV và lưu vào DB
            String cvUrl = fileUploadService.saveFile(cvFile, "cv");
            Resume resume = new Resume();
            resume.setCandidate(loggedInUser);
            resume.setFileName(cvUrl);
            resume.setTitle(cvFile.getOriginalFilename());
            resume.setUploadDate(new Date());
            resumeRepository.save(resume);

            // Tạo đơn ứng tuyển
            Application application = new Application();
            application.setJob(jobOptional.get());
            application.setCandidate(loggedInUser);
            application.setResume(resume);
            application.setCoverLetter(coverLetter);
            applicationRepository.save(application);

            redirectAttributes.addFlashAttribute("success", "Ứng tuyển thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải CV. Vui lòng thử lại!");
        }

        return "redirect:/job/" + jobId;
    }

    // Lưu / Bỏ lưu việc làm
    @PostMapping("/job/save")
    public String toggleSaveJob(@RequestParam("jobId") Integer jobId,
                                @RequestParam(value = "from", required = false) String from,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        com.polyjobs.dto.UserDTO loggedInUserDTO = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        User loggedInUser = loggedInUserDTO != null ? userService.findEntityById(loggedInUserDTO.getId()) : null;

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để lưu công việc.");
            return "redirect:/login";
        }

        if (Boolean.TRUE.equals(loggedInUser.getRole()) || Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "Nhà tuyển dụng hoặc quản trị viên không thể sử dụng chức năng này.");
            return "redirect:/job/" + jobId;
        }

        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            Optional<SavedJob> savedJobOptional = savedJobRepository.findByCandidateAndJob(loggedInUser, job);

            if (savedJobOptional.isPresent()) {
                savedJobRepository.delete(savedJobOptional.get());
                redirectAttributes.addFlashAttribute("success", "Đã bỏ lưu công việc!");
            } else {
                SavedJob savedJob = new SavedJob();
                savedJob.setCandidate(loggedInUser);
                savedJob.setJob(job);
                savedJobRepository.save(savedJob);
                redirectAttributes.addFlashAttribute("success", "Đã lưu công việc thành công!");
            }
        }

        if ("saved-jobs".equals(from)) {
            return "redirect:/saved-jobs";
        }
        return "redirect:/job/" + jobId;
    }
}