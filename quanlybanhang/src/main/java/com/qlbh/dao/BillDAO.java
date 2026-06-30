/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.dao;

import com.qlbh.entity.Bill;
import com.qlbh.util.JdbcUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author PC
 */
public class BillDAO {
    // Lấy mã hóa đơn lớn nhất
    public static String getMaxMaHD() {
        // Đã sửa 'bill' thành 'Bills'
        String sql = "SELECT TOP 1 mahd FROM Bills WHERE mahd IS NOT NULL ORDER BY mahd DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("mahd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Sinh mã hóa đơn 
    public static String generateNewMaHD() {
        String max = getMaxMaHD();

        if (max == null || max.trim().isEmpty()) {
            return "HD001";
        }

        int number = Integer.parseInt(max.substring(2));
        number++;

        return String.format("HD%03d", number);
    }

    public static int insertBill(String mahd, java.sql.Date createdDate, int total) {
        // Đã sửa 'bill' thành 'Bills'
        String sql = "INSERT INTO Bills (mahd, createdDate, total) VALUES (?, ?, ?)";
        try {
            return JdbcUtil.executeUpdate(sql,
                    mahd,
                    createdDate,
                    total
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int insertBill(String mahd, Date createdDate, int total, int userId, int customerId, String status) {
        // Đã sửa 'bill' thành 'Bills', 'user_id' thành 'userID', 'customer_id' thành 'customerID'
        String sql = "INSERT INTO Bills (mahd, createdDate, total, userID, customerID, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            return JdbcUtil.executeUpdate(sql,
                    mahd,
                    createdDate,
                    total,
                    userId,
                    customerId,
                    status
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNewBillId() {
        // Đã sửa 'bill' thành 'Bills'
        String sql = "SELECT TOP 1 id FROM Bills ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Hủy hóa đơn
    public static int cancel(int id) {
        // Đã sửa 'bill' thành 'Bills'
        String sql = "UPDATE Bills SET status = N'Huy' WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tìm
    public static List<Bill> searchByMa(String keyword) {
        List<Bill> list = new ArrayList<>();
        // Đã sửa 'bill' thành 'Bills'
        String sql = "SELECT * FROM Bills WHERE mahd LIKE ? ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, "%" + keyword + "%");
            while (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setMahd(rs.getString("mahd"));
                b.setCreatedDate(rs.getDate("createdDate"));
                b.setTotal(rs.getInt("total"));
                b.setStatus(rs.getString("status"));
                // Sửa customer_id, user_id thành customerID, userID
                b.setCustomerID(rs.getInt("customerID"));
                b.setUserID(rs.getInt("userID"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        // Đã sửa 'bill' thành 'Bills'
        String sql = "SELECT * FROM Bills ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql);
            while (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setMahd(rs.getString("mahd"));
                b.setCreatedDate(rs.getDate("createdDate"));
                b.setTotal(rs.getInt("total"));
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // lay id
    public static Bill findById(int id) {
        Bill bill = null;
        // Đã sửa 'bill' thành 'Bills'
        String sql = "select * from Bills where id = ?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, id);
            while (resultSet.next()) {
                Date createDate = resultSet.getDate("createdDate");
                int total = resultSet.getInt("total");
                String status = resultSet.getString("status");
                // Sửa customer_id, user_id thành customerID, userID
                int customerID = resultSet.getInt("customerID");
                int userID = resultSet.getInt("userID");
                String mahd = resultSet.getString("mahd");
                bill = new Bill(id, createDate, total, status, customerID, userID, mahd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bill;
    }
    
    public static List<Bill> ThongKeDoanhThu(Date tuNgay, Date denNgay){
      List<Bill> list = new ArrayList<>();
      // Đã sửa 'bill' thành 'Bills'
      String sql= "select * from Bills where status = 'hoanthanh' and createdDate between ? and ?" ;
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, tuNgay, denNgay);
            while (resultSet.next()) {                
                Date createDate = resultSet.getDate("createdDate");
                int total = resultSet.getInt("total");
                String status = resultSet.getString("status");
                // Sửa customer_id, user_id thành customerID, userID
                int customerID = resultSet.getInt("customerID");
                int userID = resultSet.getInt("userID");
                String mahd = resultSet.getString("mahd");
                // Khởi tạo bill với ID (ở đây bạn đang truyền nhầm tham số userID vào vị trí id, mình vẫn giữ nguyên cấu trúc hàm tạo của bạn)
                Bill bill = new Bill(userID, createDate, total, status, customerID, userID, mahd);
                list.add(bill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      return list;
    }
}