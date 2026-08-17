package com.polyjobs.controller;

import com.polyjobs.entity.Job;
import com.polyjobs.entity.Post;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.PostRepository;
import com.polyjobs.repository.ResumeRepository;
import com.polyjobs.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    @Autowired private com.polyjobs.service.UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private ResumeRepository resumeRepository;
    @Autowired private PostRepository postRepository;

    private boolean checkAdmin(HttpSession session) {
        com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        User u = dto != null ? userService.findEntityById(dto.getId()) : null;
        return u != null && Boolean.TRUE.equals(u.getIsAdmin());
    }

    private List<Integer> pageRange(int current, int total) {
        List<Integer> pages = new ArrayList<>();
        if (total <= 0) return pages;
        int start = Math.max(0, current - 2);
        int end   = Math.min(total - 1, current + 2);
        if (end - start < 4) {
            if (start == 0) end   = Math.min(total - 1, 4);
            else            start = Math.max(0, end - 4);
        }
        for (int i = start; i <= end; i++) pages.add(i);
        return pages;
    }

    private void addPageMeta(Model model, Page<?> p, int current) {
        model.addAttribute("currentPage",   current);
        model.addAttribute("totalPages",    p.getTotalPages());
        model.addAttribute("totalElements", p.getTotalElements());
        model.addAttribute("hasNext",       p.hasNext());
        model.addAttribute("hasPrev",       p.hasPrevious());
        model.addAttribute("pageNumbers",   pageRange(current, p.getTotalPages()));
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        model.addAttribute("totalUsers",     userRepository.count());
        model.addAttribute("totalCandidates",userRepository.findByRole(false).size());
        model.addAttribute("totalEmployers", userRepository.findByRole(true).size());
        model.addAttribute("totalJobs",      jobRepository.count());
        model.addAttribute("totalCompanies", companyRepository.count());
        model.addAttribute("totalResumes",   resumeRepository.count());
        model.addAttribute("totalPosts",     postRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(value = "page",   defaultValue = "0")  int page,
                        @RequestParam(value = "search", required = false)     String search,
                        HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE, Sort.by("id").descending());
        Page<User> userPage;
        if (search != null && !search.isBlank()) {
            userPage = userRepository.findByFullnameContainingIgnoreCaseOrUsernameContainingIgnoreCase(search, search, pr);
            model.addAttribute("search", search);
        } else {
            userPage = userRepository.findAll(pr);
        }
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("baseUrl", "/admin/users?" + (search != null && !search.isBlank() ? "search=" + search + "&" : ""));
        addPageMeta(model, userPage, page);
        return "admin/users";
    }

    @PostMapping("/users/toggle-block/{id}")
    public String toggleBlockUser(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
            User me = dto != null ? userService.findEntityById(dto.getId()) : null;
            if (me != null && user.getId().equals(me.getId())) {
                ra.addFlashAttribute("error", "Khong the tu chan chinh minh!");
                return "redirect:/admin/users";
            }
            user.setIsActive(user.getIsActive() == null || !user.getIsActive());
            userRepository.save(user);
            ra.addFlashAttribute("success", "Da " + (user.getIsActive() ? "bo chan" : "chan") + ": " + user.getUsername());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String jobs(@RequestParam(value = "page", defaultValue = "0") int page, HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE, Sort.by("id").descending());
        Page<Job> jobPage = jobRepository.findAll(pr);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("baseUrl", "/admin/jobs?");
        addPageMeta(model, jobPage, page);
        return "admin/jobs";
    }

    @PostMapping("/jobs/toggle-hide/{id}")
    public String toggleHideJob(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            job.setIsHidden(job.getIsHidden() == null || !job.getIsHidden());
            jobRepository.save(job);
            ra.addFlashAttribute("success", "Da " + (job.getIsHidden() ? "an" : "hien") + " tin: " + job.getTitle());
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(value = "type", required = false)   String type,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";
        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE);
        Page<Post> postPage;
        if (type != null && !type.isBlank()) {
            postPage = postRepository.findByTypeOrderByCreatedDateDesc(type, pr);
            model.addAttribute("selectedType", type);
        } else {
            postPage = postRepository.findAllByOrderByCreatedDateDesc(pr);
            model.addAttribute("selectedType", "ALL");
        }
        List<Post> posts = postPage.getContent();
        model.addAttribute("posts", posts);
        model.addAttribute("totalPosts",  postRepository.count());
        model.addAttribute("hiddenCount", posts.stream().filter(p -> Boolean.TRUE.equals(p.getIsHidden())).count());
        model.addAttribute("baseUrl", "/admin/posts?" + (type != null && !type.isBlank() ? "type=" + type + "&" : ""));
        addPageMeta(model, postPage, page);
        return "admin/posts";
    }

    @PostMapping("/posts/toggle-hide/{id}")
    public String toggleHidePost(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes ra) {
        if (!checkAdmin(session)) return "redirect:/login";
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setIsHidden(!Boolean.TRUE.equals(post.getIsHidden()));
            postRepository.save(post);
            String action = Boolean.TRUE.equals(post.getIsHidden()) ? "An" : "Hien lai";
            ra.addFlashAttribute("success", action + " bai viet: " + post.getTitle());
        } else {
            ra.addFlashAttribute("error", "Khong tim thay bai viet!");
        }
        return "redirect:/admin/posts";
    }
}
