package com.polyjobs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.CategoryRepository;

@Controller
public class HomeController {

    // Tiêm JobRepository vào để lấy dữ liệu
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy toàn bộ danh sách công việc từ Database và gửi sang giao diện với tên
        // "jobs"
        model.addAttribute("jobs", jobRepository.findAll());

        // Thống kê số lượng cho hero section
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("totalCompanies", companyRepository.count());

        // Trả về file giao diện có tên là index.html
        return "index";
    }
}