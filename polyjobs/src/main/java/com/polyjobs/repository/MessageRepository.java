package com.polyjobs.repository;

import com.polyjobs.entity.Message;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Lấy tất cả tin nhắn giữa 2 người, sắp xếp tăng dần theo thời gian (để hiển thị theo thứ tự từ trên xuống dưới)
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    // Lấy danh sách những người dùng đã từng nhắn tin với user hiện tại
    @Query("SELECT DISTINCT u FROM User u WHERE u IN (SELECT m.sender FROM Message m WHERE m.receiver = :user) OR u IN (SELECT m.receiver FROM Message m WHERE m.sender = :user)")
    List<User> findChattedUsers(@Param("user") User user);

    // Đếm tổng số tin nhắn chưa đọc
    long countByReceiverAndIsReadFalse(User receiver);

    // Đếm số tin nhắn chưa đọc từ 1 người cụ thể
    long countBySenderAndReceiverAndIsReadFalse(User sender, User receiver);
}
