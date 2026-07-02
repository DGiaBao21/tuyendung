package com.polyjobs.controller;

import com.polyjobs.entity.Resume;
import com.polyjobs.entity.User;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CandidateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/candidates")
    public String candidatesList(
            @RequestParam(value = "profession", required = false) String profession,
            Model model) {

        List<User> candidates;

        if (profession != null && !profession.trim().isEmpty()) {
            candidates = userRepository.findByRoleAndProfessionContainingIgnoreCase(false, profession.trim());
        } else {
            candidates = userRepository.findByRole(false);
        }

        // Tạo map để ánh xạ User ID sang CV mới nhất của họ
        Map<Integer, Resume> candidateCvMap = new HashMap<>();
        for (User candidate : candidates) {
            List<Resume> resumes = resumeRepository.findByCandidateOrderByUploadDateDesc(candidate);
            if (resumes != null && !resumes.isEmpty()) {
                candidateCvMap.put(candidate.getId(), resumes.get(0));
            }
        }

        model.addAttribute("candidates", candidates);
        model.addAttribute("candidateCvMap", candidateCvMap);
        model.addAttribute("keyword", profession);

        return "candidates";
    }
}
