/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.dao;

import com.qlbh.entity.Customer;
import com.qlbh.util.JdbcUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author PC
 */
public class CustomerDAO {
    //INSERT
    public static int insert(String fullname, String email, String phone, Boolean gender, java.sql.Date birthday) {
        String sql = "INSERT INTO customer (fullname, email, phone, gender, birthday) VALUES (?, ?, ?, ?, ?)";
        try {
            return JdbcUtil.executeUpdate(sql, fullname, email, phone, gender, birthday);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // UPDATE 
    public static int update(int id, String fullname, String email, String phone, Boolean gender, java.sql.Date birthday) {
        String sql = "UPDATE customer SET fullname = ?, email = ?, phone = ?, gender = ?, birthday = ? WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, fullname, email, phone, gender, birthday, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //DELETE =
    public static int delete(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //load bảng
    public static List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY id DESC";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql);
            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getBoolean("gender"),
                        rs.getDate("birthday")
                );
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //
    public static Customer findByPhone(String phone) {
        Customer customer = null;
        String sqString = "SELECT * FROM customer WHERE phone = ?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sqString, phone);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullname = resultSet.getString("fullname");
                String email = resultSet.getString("email");
                boolean gender = resultSet.getBoolean("gender");
                Date birthDay = resultSet.getDate("birthday");
                customer = new Customer(id, fullname, email, phone, gender, birthDay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }

    // 
    public static Customer findById(int id) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, id);
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getBoolean("gender"),
                        rs.getDate("birthday")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
