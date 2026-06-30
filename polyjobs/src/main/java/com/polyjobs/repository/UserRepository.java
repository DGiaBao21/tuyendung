package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.polyjobs.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Hàm tự chế: Spring sẽ tự động hiểu bạn muốn tìm User theo username
    User findByUsername(String username); 
}