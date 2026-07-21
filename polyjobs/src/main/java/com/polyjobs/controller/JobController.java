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
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    // Đường dẫn có chứa ID của công việc (Ví dụ: /job/1)
    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        // Tìm công việc theo ID
        Optional<Job> jobOptional = jobRepository.findById(id);
        
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            model.addAttribute("job", job);
            
            // Lấy resumes của candidate đang đăng nhập
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser != null && Boolean.FALSE.equals(loggedInUser.getRole()) && !Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
                List<Resume> resumes = resumeRepository.findByCandidateOrderByUploadDateDesc(loggedInUser);
                model.addAttribute("myResumes", resumes);
                
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
            // Nếu không tìm thấy (ID sai), đá về trang chủ
            return "redirect:/";
        }
    }

    @PostMapping("/job/apply")
    public String applyJob(@RequestParam("jobId") Integer jobId,
                           @RequestParam("resumeId") Integer resumeId,
                           @RequestParam(value = "coverLetter", required = false) String coverLetter,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để ứng tuyển.");
            return "redirect:/login";
        }

        if (Boolean.TRUE.equals(loggedInUser.getRole()) || Boolean.TRUE.equals(loggedInUser.getIsAdmin())) {
            redirectAttributes.addFlashAttribute("error", "Nhà tuyển dụng hoặc quản trị viên không thể ứng tuyển.");
            return "redirect:/job/" + jobId;
        }

        Optional<Job> jobOptional = jobRepository.findById(jobId);
        Optional<Resume> resumeOptional = resumeRepository.findById(resumeId);

        if (jobOptional.isPresent() && resumeOptional.isPresent()) {
            Application application = new Application();
            application.setJob(jobOptional.get());
            application.setCandidate(loggedInUser);
            application.setResume(resumeOptional.get());
            application.setCoverLetter(coverLetter);
            
            applicationRepository.save(application);
            redirectAttributes.addFlashAttribute("success", "Ứng tuyển thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra, không tìm thấy công việc hoặc CV.");
        }

        return "redirect:/job/" + jobId;
    }

    @PostMapping("/job/save")
    public String toggleSaveJob(@RequestParam("jobId") Integer jobId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
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
                // Đã lưu -> Hủy lưu
                savedJobRepository.delete(savedJobOptional.get());
                redirectAttributes.addFlashAttribute("success", "Đã bỏ lưu công việc!");
            } else {
                // Chưa lưu -> Lưu
                SavedJob savedJob = new SavedJob();
                savedJob.setCandidate(loggedInUser);
                savedJob.setJob(job);
                savedJobRepository.save(savedJob);
                redirectAttributes.addFlashAttribute("success", "Đã lưu công việc thành công!");
            }
        }
        
        return "redirect:/job/" + jobId;
    }
}