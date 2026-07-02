package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    // Đường dẫn có chứa ID của công việc (Ví dụ: /job/1)
    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable("id") Integer id, Model model) {
        // Tìm công việc theo ID
        Optional<Job> jobOptional = jobRepository.findById(id);
        
        if (jobOptional.isPresent()) {
            // Nếu tìm thấy, gửi dữ liệu sang giao diện job-detail.html
            model.addAttribute("job", jobOptional.get());
            return "job-detail"; 
        } else {
            // Nếu không tìm thấy (ID sai), đá về trang chủ
            return "redirect:/";
        }
    }
}