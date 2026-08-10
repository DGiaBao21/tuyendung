package com.polyjobs.controller;

import com.polyjobs.entity.*;
import com.polyjobs.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private com.polyjobs.service.UserService userService;

    private User getLoggedInUser(HttpSession session) {
        com.polyjobs.dto.UserDTO dto = (com.polyjobs.dto.UserDTO) session.getAttribute("loggedInUser");
        return dto != null ? userService.findEntityById(dto.getId()) : null;
    }

    // ═══ Trang cộng đồng ═══
    @GetMapping("")
    public String communityPage(
            @RequestParam(value = "type",    required = false)                   String type,
            @RequestParam(value = "hashtag", required = false)                   String hashtag,
            @RequestParam(value = "tab",     required = false, defaultValue = "feed") String tab,
            @RequestParam(value = "mine",    required = false, defaultValue = "false") boolean mine,
            @RequestParam(value = "page",    required = false, defaultValue = "0") int page,
            HttpSession session, Model model) {

        User loggedInUser = getLoggedInUser(session);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("activeTab", tab);
        model.addAttribute("selectedType", type != null ? type : "ALL");
        model.addAttribute("selectedHashtag", hashtag);
        model.addAttribute("isMineView", mine);
        model.addAttribute("currentPage", page);

        final int PAGE_SIZE = 8;
        org.springframework.data.domain.Pageable pageable =
            org.springframework.data.domain.PageRequest.of(Math.max(0, page), PAGE_SIZE);

        org.springframework.data.domain.Page<Post> postPage;

        if (mine && loggedInUser != null) {
            postPage = postRepository.findByAuthorOrderByCreatedDateDesc(loggedInUser, pageable);
        } else if ("trending".equals(tab)) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            postPage = postRepository.findTrending(cal.getTime(), pageable);
        } else if (hashtag != null && !hashtag.isBlank()) {
            postPage = postRepository.findByHashtagContaining(hashtag.toLowerCase().trim(), pageable);
        } else if (type != null && !type.isBlank()) {
            postPage = postRepository.findByTypeAndIsHiddenFalseOrderByCreatedDateDesc(type, pageable);
        } else {
            postPage = postRepository.findByIsHiddenFalseOrderByCreatedDateDesc(pageable);
        }

        List<Post> posts = postPage.getContent();
        model.addAttribute("posts", posts);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalElements", postPage.getTotalElements());
        model.addAttribute("hasNext", postPage.hasNext());
        model.addAttribute("hasPrev", postPage.hasPrevious());
        model.addAttribute("pageNumbers", generatePageRange(page, postPage.getTotalPages()));

        // Thông tin like của user hiện tại
        if (loggedInUser != null) {
            Set<Integer> likedPostIds = postLikeRepository.findAll().stream()
                    .filter(l -> l.getUser().getId().equals(loggedInUser.getId()))
                    .map(l -> l.getPost().getId())
                    .collect(Collectors.toSet());
            model.addAttribute("likedPostIds", likedPostIds);
        } else {
            model.addAttribute("likedPostIds", new HashSet<>());
        }

        // Comments cho từng bài
        Map<Integer, List<PostComment>> commentsMap = new LinkedHashMap<>();
        for (Post post : posts) {
            commentsMap.put(post.getId(), postCommentRepository.findByPostOrderByCreatedDateAsc(post));
        }
        model.addAttribute("commentsMap", commentsMap);

        // Top trending hashtags (sidebar)
        try {
            List<String> topTags = postRepository.findTopHashtags();
            model.addAttribute("topHashtags", topTags.stream().limit(10).collect(Collectors.toList()));
        } catch (Exception e) {
            model.addAttribute("topHashtags", new ArrayList<>());
        }

        return "community";
    }

    /** Tạo danh sách trang hiển thị: tối đa 5 số xung quanh trang hiện tại */
    private List<Integer> generatePageRange(int current, int total) {
        List<Integer> pages = new ArrayList<>();
        if (total <= 0) return pages;
        int start = Math.max(0, current - 2);
        int end   = Math.min(total - 1, current + 2);
        // Đảm bảo luôn hiện 5 số nếu đủ trang
        if (end - start < 4) {
            if (start == 0) end   = Math.min(total - 1, 4);
            else            start = Math.max(0, end - 4);
        }
        for (int i = start; i <= end; i++) pages.add(i);
        return pages;
    }

    // ═══ Đăng bài mới ═══
    @PostMapping("/post")
    public String createPost(@RequestParam("title")   String title,
                             @RequestParam("content") String content,
                             @RequestParam("type")    String type,
                             @RequestParam(value = "hashtags", required = false) String rawHashtags,
                             HttpSession session, RedirectAttributes redirectAttributes) {

        User user = getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đăng bài!");
            return "redirect:/community";
        }

        boolean isEmployer = Boolean.TRUE.equals(user.getRole());
        if (isEmployer  && "TIM_VIEC".equals(type)) {
            redirectAttributes.addFlashAttribute("error", "Nhà tuyển dụng không thể đăng bài Tìm việc!");
            return "redirect:/community";
        }
        if (!isEmployer && "TUYEN_DUNG".equals(type)) {
            redirectAttributes.addFlashAttribute("error", "Ứng viên không thể đăng bài Tuyển dụng!");
            return "redirect:/community";
        }
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Tiêu đề và nội dung không được để trống!");
            return "redirect:/community";
        }

        // Parse hashtags: "#java #spring boot" → "java,spring,boot"
        String parsedHashtags = "";
        if (rawHashtags != null && !rawHashtags.isBlank()) {
            parsedHashtags = Arrays.stream(rawHashtags.split("[\\s,]+"))
                    .map(t -> t.replaceAll("[^a-zA-Z0-9àáâãèéêìíòóôõùúăđĩũơưạảấầẩẫậắằẳẵặẹẻẽếềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹ]", ""))
                    .map(t -> t.startsWith("#") ? t.substring(1) : t)
                    .filter(t -> !t.isBlank())
                    .map(String::toLowerCase)
                    .distinct()
                    .limit(5)
                    .collect(Collectors.joining(","));
        }

        Post post = new Post();
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setType(type);
        post.setHashtags(parsedHashtags.isBlank() ? null : parsedHashtags);
        post.setAuthor(user);
        post.setCreatedDate(new Date());
        postRepository.save(post);

        redirectAttributes.addFlashAttribute("success", "Đăng bài thành công!");
        return "redirect:/community";
    }

    // ═══ Toggle Like (AJAX) ═══
    @PostMapping("/like/{postId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Integer postId, HttpSession session) {
        User user = getLoggedInUser(session);
        Map<String, Object> res = new HashMap<>();

        if (user == null) {
            res.put("error", "Chưa đăng nhập");
            return ResponseEntity.status(401).body(res);
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            res.put("error", "Không tìm thấy bài viết");
            return ResponseEntity.status(404).body(res);
        }

        Optional<PostLike> existing = postLikeRepository.findByUserAndPost(user, post);
        boolean liked;
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            liked = false;
        } else {
            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);
            postLikeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
            liked = true;
        }
        postRepository.save(post);

        res.put("liked", liked);
        res.put("count", post.getLikesCount());
        return ResponseEntity.ok(res);
    }

    // ═══ Đăng comment ═══
    @PostMapping("/comment/{postId}")
    public String addComment(@PathVariable Integer postId,
                             @RequestParam("content") String content,
                             HttpSession session, RedirectAttributes redirectAttributes) {

        User user = getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để bình luận!");
            return "redirect:/community";
        }
        if (content == null || content.isBlank()) {
            return "redirect:/community";
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return "redirect:/community";

        PostComment comment = new PostComment();
        comment.setContent(content.trim());
        comment.setAuthor(user);
        comment.setPost(post);
        postCommentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return "redirect:/community#post-" + postId;
    }

    // ═══ Xóa comment ═══
    @PostMapping("/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Integer commentId,
                                HttpSession session, RedirectAttributes redirectAttributes) {

        User user = getLoggedInUser(session);
        if (user == null) return "redirect:/login";

        PostComment comment = postCommentRepository.findById(commentId).orElse(null);
        if (comment == null) return "redirect:/community";

        boolean isOwner = comment.getAuthor().getId().equals(user.getId());
        boolean isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
        if (!isOwner && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa comment này!");
            return "redirect:/community";
        }

        Post post = comment.getPost();
        postCommentRepository.delete(comment);
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);

        redirectAttributes.addFlashAttribute("success", "Đã xóa bình luận!");
        return "redirect:/community#post-" + post.getId();
    }

    // ═══ Xóa bài (chính chủ) ═══
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id,
                             HttpSession session, RedirectAttributes redirectAttributes) {

        User user = getLoggedInUser(session);
        if (user == null) return "redirect:/login";

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            redirectAttributes.addFlashAttribute("error", "Bài viết không tồn tại!");
            return "redirect:/community";
        }

        boolean isOwner = post.getAuthor().getId().equals(user.getId());
        boolean isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
        if (!isOwner && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa bài này!");
            return "redirect:/community";
        }

        postRepository.delete(post);
        redirectAttributes.addFlashAttribute("success", "Đã xóa bài viết!");
        return "redirect:/community";
    }
}
