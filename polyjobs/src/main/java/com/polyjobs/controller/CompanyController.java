package com.polyjobs.controller;

import com.polyjobs.entity.Company;
import com.polyjobs.entity.Job;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    // Danh sách tất cả công ty
    @GetMapping("/companies")
    public String companies(Model model) {
        List<Company> companies = companyRepository.findAll();
        model.addAttribute("companies", companies);
        return "companies";
    }

    // Chi tiết công ty
    @GetMapping("/company/{id}")
    public String companyDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Company> companyOptional = companyRepository.findById(id);

        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            List<Job> companyJobs = jobRepository.findByCompanyId(id);

            model.addAttribute("company", company);
            model.addAttribute("companyJobs", companyJobs);
            return "company-detail";
        } else {
            return "redirect:/companies";
        }
    }
}
