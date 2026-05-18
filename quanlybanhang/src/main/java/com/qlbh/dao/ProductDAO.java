/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.dao;

import com.qlbh.entity.Product;
import com.qlbh.util.JdbcUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class ProductDAO {
    // THem

    public static int insert(String name, int price, int quantity, String image, int categoryId) {
        String sql = "insert into products (name, price, quantity, image, category_id)values(?, ?, ?, ?, ?)";
        try {
            int rs = JdbcUtil.executeUpdate(sql, name, price, quantity, image, categoryId);
            return rs;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

// CAP NHAT
    public static int update(Product p) {
        String sql = "UPDATE products SET name=?, price=?, quantity=?, image=?, category_id=? WHERE id=?";
        try {
            return JdbcUtil.executeUpdate(sql,
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getImage(),
                    p.getCategoryID(),
                    p.getId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
//Xóa

    public static int delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // LẤY
    public static List<Product> findALL() {
        List<Product> list = new ArrayList<>();
        // LẤY 
        String sql = "select * from products order by id desc";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int price = resultSet.getInt("price");
                int categoryId = resultSet.getInt("category_id");
                String image = resultSet.getString("image");

                Product p = new Product(id, name, quantity, price, image, categoryId);
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // lay id
    public static Product findById(int id) {
        Product product = null ;
        // LẤY 
        String sql = "select * from products where id = ?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, id);
            while (resultSet.next()) {
            
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int price = resultSet.getInt("price");
                int categoryId = resultSet.getInt("category_id");
                String image = resultSet.getString("image");

                product  = new Product(id, name, quantity, price, image, categoryId);
               
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }
    

}
