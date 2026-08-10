package com.polyjobs.controller;

import com.polyjobs.entity.Category;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @org.springframework.beans.factory.annotation.Autowired
    private com.polyjobs.service.UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    // Kiểm tra quyền Admin
    private boolean isAdmin(HttpSession session) {
        com.polyjobs.dto.UserDTO userDTO = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        User user = userDTO != null ? userService.findEntityById(userDTO.getId()) : null;
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }

    @GetMapping("/categories")
    public String listCategories(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    @GetMapping("/category/add")
    public String showAddCategoryForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Integer id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute Category category, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("success", "Đã lưu danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục này vì đang có bài đăng thuộc danh mục!");
        }
        return "redirect:/admin/categories";
    }
}
