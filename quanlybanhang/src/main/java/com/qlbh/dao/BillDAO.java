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
        String sql = "SELECT TOP 1 mahd FROM bill WHERE mahd IS NOT NULL ORDER BY mahd DESC";
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

        String sql = "INSERT INTO bill (mahd, createdDate, total) VALUES (?, ?, ?)";

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

        String sql = "INSERT INTO bill (mahd, createdDate, total, user_id, customer_id, status) VALUES (?, ?, ?,?,?,?)";

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
        String sql = "SELECT TOP 1 id FROM bill ORDER BY id DESC";
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
        String sql = "UPDATE bill SET status = N'Huy' WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
//Tìm

    public static List<Bill> searchByMa(String keyword) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bill WHERE mahd LIKE ? ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, "%" + keyword + "%");
            while (rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setMahd(rs.getString("mahd"));
                b.setCreatedDate(rs.getDate("createdDate"));
                b.setTotal(rs.getInt("total"));
                b.setStatus(rs.getString("status"));
                b.setCustomerID(rs.getInt("customer_id"));
                b.setUserID(rs.getInt("user_id"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bill ORDER BY id DESC";
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
        String sql = "select * from bill where id = ?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, id);
            while (resultSet.next()) {
                Date createDate = resultSet.getDate("createdDate");
                int total = resultSet.getInt("total");
                String status = resultSet.getString("status");
                int customerID = resultSet.getInt("customer_id");
                int userID = resultSet.getInt("user_id");
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
      String sql= "select * from bill where status = 'hoanthanh' and createdDate between ? and ?" ;
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, tuNgay, denNgay);
            while (resultSet.next()) {                
                Date createDate = resultSet.getDate("createdDate");
                int total = resultSet.getInt("total");
                String status = resultSet.getString("status");
                int customerID = resultSet.getInt("customer_id");
                int userID = resultSet.getInt("user_id");
                String mahd = resultSet.getString("mahd");
                Bill bill = new Bill(userID, createDate, total, status, customerID, userID, mahd);
                list.add(bill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      
      
      return list;
      
    }
    
            
            
}
