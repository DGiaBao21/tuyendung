package com.polyjobs.repository;

import com.polyjobs.entity.Notification;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserOrderByCreatedDateDesc(User user);
    long countByUserAndIsRead(User user, Boolean isRead);
}
