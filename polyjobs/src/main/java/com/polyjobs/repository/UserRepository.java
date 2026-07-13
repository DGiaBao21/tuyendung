package com.polyjobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.polyjobs.entity.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm User theo username
    User findByUsername(String username);

    // Đăng nhập: tìm theo username + password
    User findByUsernameAndPassword(String username, String password);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Lấy danh sách người dùng theo vai trò
    List<User> findByRole(Boolean role);

    // Lấy danh sách người dùng theo vai trò và ngành nghề (tìm kiếm tương đối)
    List<User> findByRoleAndProfessionContainingIgnoreCase(Boolean role, String profession);

    // Lấy danh sách ngành nghề độc nhất của ứng viên (cho dropdown filter)
    @Query("SELECT DISTINCT u.profession FROM User u WHERE u.role = false AND u.profession IS NOT NULL AND u.profession <> ''")
    List<String> findDistinctProfessions();
}