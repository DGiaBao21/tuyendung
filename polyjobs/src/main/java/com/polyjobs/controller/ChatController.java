package com.polyjobs.controller;

import com.polyjobs.dto.ChatMessage;
import com.polyjobs.entity.Message;
import com.polyjobs.entity.Notification;
import com.polyjobs.entity.User;
import com.polyjobs.repository.NotificationRepository;
import com.polyjobs.repository.MessageRepository;
import com.polyjobs.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    // Hiển thị trang giao diện hộp thư
    @GetMapping("/messages")
    public String messagesPage(
            @RequestParam(value = "userId", required = false) Integer targetUserId,
            HttpSession session, Model model) {
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Lấy danh sách những người đã từng nhắn tin
        List<User> chattedUsers = new ArrayList<>(messageRepository.findChattedUsers(loggedInUser));
        
        // Nếu có truyền userId (Bấm từ nút Nhắn tin)
        if (targetUserId != null) {
            User targetUser = userRepository.findById(targetUserId).orElse(null);
            if (targetUser != null && !targetUser.getId().equals(loggedInUser.getId())) {
                boolean exists = chattedUsers.stream().anyMatch(u -> u.getId().equals(targetUser.getId()));
                if (!exists) {
                    chattedUsers.add(0, targetUser); // Thêm lên đầu danh sách
                }
                model.addAttribute("autoOpenUserId", targetUser.getId());
            }
        }
        // Tính số tin nhắn chưa đọc cho từng người
        Map<Integer, Long> unreadCounts = new HashMap<>();
        for (User u : chattedUsers) {
            long count = messageRepository.countBySenderAndReceiverAndIsReadFalse(u, loggedInUser);
            if (count > 0) {
                unreadCounts.put(u.getId(), count);
            }
        }
        
        model.addAttribute("chattedUsers", chattedUsers);
        model.addAttribute("unreadCounts", unreadCounts);
        return "messages";
    }

    // API để lấy lịch sử tin nhắn với 1 người dùng cụ thể
    @GetMapping("/api/messages/{userId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getConversation(@PathVariable("userId") Integer userId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).build();
        }

        User otherUser = userRepository.findById(userId).orElse(null);
        if (otherUser == null) {
            return ResponseEntity.notFound().build();
        }

        List<Message> messages = messageRepository.findConversation(loggedInUser, otherUser);
        
        // Chuyển đổi sang Map để tránh đệ quy vòng lặp JSON (vì User có nhiều mối quan hệ)
        List<Map<String, Object>> response = new ArrayList<>();
        for (Message m : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("senderId", m.getSender().getId());
            map.put("receiverId", m.getReceiver().getId());
            map.put("content", m.getContent());
            map.put("timestamp", m.getTimestamp());
            map.put("isRead", m.getIsRead());
            response.add(map);
            
            // Nếu người nhận là mình và tin nhắn chưa đọc -> Đánh dấu đã đọc
            if (m.getReceiver().getId().equals(loggedInUser.getId()) && !m.getIsRead()) {
                m.setIsRead(true);
                messageRepository.save(m);
            }
        }

        return ResponseEntity.ok(response);
    }

    // API đếm số tin nhắn chưa đọc (cho badge đỏ trên icon chat)
    @GetMapping("/api/messages/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).build();
        }
        long count = messageRepository.countByReceiverAndIsReadFalse(loggedInUser);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        User sender = userRepository.findById(chatMessage.getSenderId()).orElse(null);
        User receiver = userRepository.findById(chatMessage.getReceiverId()).orElse(null);

        if (sender != null && receiver != null) {
            // Lưu vào DB
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(chatMessage.getContent());
            message.setTimestamp(new Date());
            message.setIsRead(false);
            messageRepository.save(message);

            // Tạo payload gửi cho WebSocket client (người nhận)
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", message.getId());
            payload.put("senderId", sender.getId());
            payload.put("receiverId", receiver.getId());
            payload.put("content", message.getContent());
            payload.put("timestamp", message.getTimestamp());
            payload.put("senderName", sender.getFullname());
            payload.put("senderAvatar", sender.getAvatar());

            // Gửi tin nhắn tới hàng đợi riêng của người nhận
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(receiver.getId()), "/queue/messages", payload
            );
            
            // Gửi lại tin nhắn cho chính người gửi (để hiển thị lên UI ngay lập tức)
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(sender.getId()), "/queue/messages", payload
            );
        }
    }
}
