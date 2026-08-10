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

    @Autowired
    private com.polyjobs.service.UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private PostRepository postRepository;

    private static final int PAGE_SIZE = 10;

    // Middleware check admin
    private boolean checkAdmin(HttpSession session) {
        com.polyjobs.dto.UserDTO loggedInUserDTO = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        User loggedInUser = loggedInUserDTO != null ? userService.findEntityById(loggedInUserDTO.getId()) : null;
        return loggedInUser != null && loggedInUser.getIsAdmin() != null && loggedInUser.getIsAdmin();
    }

    /** Tạo dải số trang hiển thị (tối đa 5) */
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

    private void addPagination(Model model, Page<?> pg, int current) {
        model.addAttribute("currentPage",   current);
        model.addAttribute("totalPages",    pg.getTotalPages());
        model.addAttribute("totalElements", pg.getTotalElements());
        model.addAttribute("hasNext",       pg.hasNext());
        model.addAttribute("hasPrev",       pg.hasPrevious());
        model.addAttribute("pageNumbers",   pageRange(current, pg.getTotalPages()));
    }

    // ═══ Dashboard ═══
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        model.addAttribute("totalUsers",      userRepository.count());
        model.addAttribute("totalCandidates", userRepository.findByRole(false).size());
        model.addAttribute("totalEmployers",  userRepository.findByRole(true).size());
        model.addAttribute("totalJobs",       jobRepository.count());
        model.addAttribute("totalCompanies",  companyRepository.count());
        model.addAttribute("totalResumes",    resumeRepository.count());
        model.addAttribute("totalPosts",      postRepository.count());

        return "admin/dashboard";
    }

    // ═══ Quản lý người dùng ═══
    @GetMapping("/users")
    public String users(@RequestParam(value = "page", defaultValue = "0") int page,
                        HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE, Sort.by("id").descending());
        Page<User> pg  = userRepository.findAll(pr);

        model.addAttribute("users", pg.getContent());
        addPagination(model, pg, page);
        return "admin/users";
    }

    @PostMapping("/users/toggle-block/{id}")
    public String toggleBlockUser(@PathVariable("id") Integer id,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) return "redirect:/login";

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
            User me = dto != null ? userService.findEntityById(dto.getId()) : null;
            if (me != null && user.getId().equals(me.getId())) {
                redirectAttributes.addFlashAttribute("error", "Không thể tự chặn chính mình!");
                return "redirect:/admin/users?page=" + page;
            }
            user.setIsActive(user.getIsActive() == null || !user.getIsActive());
            userRepository.save(user);
            String status = user.getIsActive() ? "bỏ chặn" : "chặn";
            redirectAttributes.addFlashAttribute("success", "Đã " + status + " người dùng: " + user.getUsername());
        }
        return "redirect:/admin/users?page=" + page;
    }

    // ═══ Quản lý tin tuyển dụng ═══
    @GetMapping("/jobs")
    public String jobs(@RequestParam(value = "page", defaultValue = "0") int page,
                       HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE, Sort.by("id").descending());
        Page<Job> pg   = jobRepository.findAll(pr);

        model.addAttribute("jobs", pg.getContent());
        addPagination(model, pg, page);
        return "admin/jobs";
    }

    @PostMapping("/jobs/toggle-hide/{id}")
    public String toggleHideJob(@PathVariable("id") Integer id,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) return "redirect:/login";

        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            job.setIsHidden(job.getIsHidden() == null || !job.getIsHidden());
            jobRepository.save(job);
            String status = job.getIsHidden() ? "ẩn" : "hiện";
            redirectAttributes.addFlashAttribute("success", "Đã " + status + " tin tuyển dụng: " + job.getTitle());
        }
        return "redirect:/admin/jobs?page=" + page;
    }

    // ═══ Quản lý bài đăng cộng đồng ═══
    @GetMapping("/posts")
    public String posts(@RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        HttpSession session, Model model) {
        if (!checkAdmin(session)) return "redirect:/login";

        PageRequest pr = PageRequest.of(Math.max(0, page), PAGE_SIZE);
        Page<Post> pg;

        if (type != null && !type.isBlank()) {
            pg = postRepository.findByTypeOrderByCreatedDateDesc(type, pr);
            model.addAttribute("selectedType", type);
        } else {
            pg = postRepository.findAllByOrderByCreatedDateDesc(pr);
            model.addAttribute("selectedType", "ALL");
        }

        model.addAttribute("posts",      pg.getContent());
        model.addAttribute("totalPosts", postRepository.count());
        long hiddenCount = pg.getContent().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsHidden())).count();
        model.addAttribute("hiddenCount", hiddenCount);
        addPagination(model, pg, page);
        return "admin/posts";
    }

    @PostMapping("/posts/toggle-hide/{id}")
    public String toggleHidePost(@PathVariable("id") Integer id,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session)) return "redirect:/login";

        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setIsHidden(!Boolean.TRUE.equals(post.getIsHidden()));
            postRepository.save(post);
            String action = Boolean.TRUE.equals(post.getIsHidden()) ? "Ẩn" : "Hiện lại";
            redirectAttributes.addFlashAttribute("success", action + " bài viết: \"" + post.getTitle() + "\"");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài viết!");
        }
        return "redirect:/admin/posts?page=" + page;
    }
}
