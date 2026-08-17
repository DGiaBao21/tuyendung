package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.entity.Category;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class JobListController {

    private static final int PAGE_SIZE = 9;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/jobs")
    public String jobs(
            @RequestParam(value = "keyword",    required = false) String keyword,
            @RequestParam(value = "location",   required = false) String location,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "salary",     required = false) String salary,
            @RequestParam(value = "page",       defaultValue = "0") int page,
            Model model) {

        int safePage = Math.max(0, page);
        PageRequest pr = PageRequest.of(safePage, PAGE_SIZE, Sort.by("id").descending());

        Page<Job> jobPage = jobRepository.searchJobsPaged(keyword, location, categoryId, salary, pr);

        List<Category> categories = categoryRepository.findAll();

        // Tao danh sach so trang hien thi (toi da 5 so)
        int totalPages = jobPage.getTotalPages();
        List<Integer> pageNumbers = new ArrayList<>();
        if (totalPages > 0) {
            int start = Math.max(0, safePage - 2);
            int end   = Math.min(totalPages - 1, safePage + 2);
            if (end - start < 4) {
                if (start == 0) end   = Math.min(totalPages - 1, 4);
                else            start = Math.max(0, end - 4);
            }
            for (int i = start; i <= end; i++) pageNumbers.add(i);
        }

        // Xay dung baseUrl giu nguyen bo loc khi chuyen trang
        StringBuilder baseUrl = new StringBuilder("/jobs?");
        if (keyword  != null && !keyword.isBlank())  baseUrl.append("keyword=").append(java.net.URLEncoder.encode(keyword, java.nio.charset.StandardCharsets.UTF_8)).append("&");
        if (location != null && !location.isBlank()) baseUrl.append("location=").append(java.net.URLEncoder.encode(location, java.nio.charset.StandardCharsets.UTF_8)).append("&");
        if (categoryId != null) baseUrl.append("categoryId=").append(categoryId).append("&");
        if (salary   != null && !salary.isBlank())   baseUrl.append("salary=").append(java.net.URLEncoder.encode(salary, java.nio.charset.StandardCharsets.UTF_8)).append("&");

        model.addAttribute("jobs",          jobPage.getContent());
        model.addAttribute("categories",    categories);
        model.addAttribute("keyword",       keyword);
        model.addAttribute("location",      location);
        model.addAttribute("categoryId",    categoryId);
        model.addAttribute("salary",        salary);
        model.addAttribute("totalResults",  jobPage.getTotalElements());
        model.addAttribute("currentPage",   safePage);
        model.addAttribute("totalPages",    totalPages);
        model.addAttribute("hasNext",       jobPage.hasNext());
        model.addAttribute("hasPrev",       jobPage.hasPrevious());
        model.addAttribute("pageNumbers",   pageNumbers);
        model.addAttribute("baseUrl",       baseUrl.toString());

        return "jobs";
    }
}
