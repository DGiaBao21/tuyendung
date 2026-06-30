package com.polyjobs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.polyjobs.repository.JobRepository;

@Controller
public class HomeController {

    // Tiêm JobRepository vào để lấy dữ liệu
    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy toàn bộ danh sách công việc từ Database và gửi sang giao diện với tên
        // "jobs"
        model.addAttribute("jobs", jobRepository.findAll());

        // Trả về file giao diện có tên là index.html
        return "index";
    }
}