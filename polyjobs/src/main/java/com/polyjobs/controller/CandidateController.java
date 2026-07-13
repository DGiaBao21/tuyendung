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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CandidateController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/candidates")
    public String candidatesList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "profession", required = false) String profession,
            Model model) {

        boolean hasKeyword  = keyword   != null && !keyword.trim().isEmpty();
        boolean hasProfession = profession != null && !profession.trim().isEmpty();

        // Lấy tất cả ứng viên (role = false) rồi lọc phía Java
        List<User> all = userRepository.findByRole(false);

        List<User> candidates = all.stream().filter(u -> {
            boolean matchKw = true;
            boolean matchPr = true;
            if (hasKeyword) {
                String kw = keyword.trim().toLowerCase();
                matchKw = (u.getFullname()  != null && u.getFullname().toLowerCase().contains(kw))
                       || (u.getProfession() != null && u.getProfession().toLowerCase().contains(kw));
            }
            if (hasProfession) {
                matchPr = u.getProfession() != null &&
                          u.getProfession().toLowerCase().contains(profession.trim().toLowerCase());
            }
            return matchKw && matchPr;
        }).collect(Collectors.toList());

        // Map User ID -> CV mới nhất
        Map<Integer, Resume> candidateCvMap = new HashMap<>();
        for (User candidate : candidates) {
            List<Resume> resumes = resumeRepository.findByCandidateOrderByUploadDateDesc(candidate);
            if (resumes != null && !resumes.isEmpty()) {
                candidateCvMap.put(candidate.getId(), resumes.get(0));
            }
        }

        // Danh sách ngành nghề cho dropdown — lọc từ danh sách tất cả ứng viên
        List<String> professions = all.stream()
                .map(User::getProfession)
                .filter(p -> p != null && !p.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("candidates", candidates);
        model.addAttribute("candidateCvMap", candidateCvMap);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedProfession", profession);
        model.addAttribute("professions", professions);
        model.addAttribute("totalResults", candidates.size());

        return "candidates";
    }
}
