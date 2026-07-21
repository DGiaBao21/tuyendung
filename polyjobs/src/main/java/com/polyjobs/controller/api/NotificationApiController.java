package com.polyjobs.controller.api;

import com.polyjobs.entity.Notification;
import com.polyjobs.entity.User;
import com.polyjobs.repository.NotificationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<?> getNotifications(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedDateDesc(loggedInUser);
        long unreadCount = notificationRepository.countByUserAndIsRead(loggedInUser, false);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable("id") Integer id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null && notification.getUser().getId().equals(loggedInUser.getId())) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.status(404).body("Not found");
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedDateDesc(loggedInUser);
        for (Notification n : notifications) {
            if (!n.getIsRead()) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        }
        return ResponseEntity.ok("Success");
    }
}
