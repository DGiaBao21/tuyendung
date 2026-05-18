package com.qlbh.dao;

import com.qlbh.entity.User;
import com.qlbh.util.JdbcUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Lấy user theo username (đăng nhập)
    public static User findByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, username);
            while (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullname"),
                        rs.getBoolean("gender"),
                        rs.getBoolean("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Lấy user theo ID
    public static User findById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, id);
            while (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullname"),
                        rs.getBoolean("gender"),
                        rs.getBoolean("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Lấy toàn bộ danh sách nhân viên (load bảng)
    public static List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql);
            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullname"),
                        rs.getBoolean("gender"),
                        rs.getBoolean("role")
                );
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhân viên
    public static int insert(User u) {
        String sql = "INSERT INTO users(username, password, fullname, gender, role) VALUES (?, ?, ?, ?, ?)";
        try {
            return JdbcUtil.executeUpdate(sql,
                    u.getUsername(),
                    u.getPassword(),
                    u.getFullname(),
                    u.getGender(),
                    u.getRole()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Sửa nhân viên
public static int update(User u) {
    String sql = "UPDATE users SET username=?, password=?, fullname=?, gender=?, role=? WHERE id=?";
    try {
        return JdbcUtil.executeUpdate(sql,
                u.getUsername(),
                u.getPassword(),
                u.getFullname(),
                u.getGender(),
                u.getRole(),
                u.getId()
        );
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}


    // Xóa nhân viên
    public static int delete(int id) {
       String sql = "DELETE FROM users WHERE id = ?";
    try {
        return JdbcUtil.executeUpdate(sql, id);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}
}
