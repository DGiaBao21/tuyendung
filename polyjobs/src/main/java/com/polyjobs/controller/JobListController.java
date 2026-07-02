package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.repository.JobRepository;
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

    @GetMapping("/jobs")
    public String jobs(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            Model model) {

        List<Job> jobs;

        // Logic tìm kiếm
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();

        if (hasKeyword && hasLocation) {
            jobs = jobRepository.findByTitleContainingIgnoreCaseAndLocation(keyword.trim(), location.trim());
        } else if (hasKeyword) {
            jobs = jobRepository.findByTitleContainingIgnoreCase(keyword.trim());
        } else if (hasLocation) {
            jobs = jobRepository.findByLocation(location.trim());
        } else {
            jobs = jobRepository.findAll();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("totalResults", jobs.size());

        return "jobs";
    }
}
