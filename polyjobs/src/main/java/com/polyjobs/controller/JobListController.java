package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.entity.Category;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class JobListController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/jobs")
    public String jobs(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "salary", required = false) String salary,
            Model model) {

        List<Job> jobs = jobRepository.searchJobs(keyword, location, categoryId, salary);
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("salary", salary);
        model.addAttribute("totalResults", jobs.size());

        return "jobs";
    }
}
