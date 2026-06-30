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
    // THÊM
    public static int insert(String name, int price, int quantity, String image, int categoryId) {
        String sql = "insert into products (name, price, quantity, image, categoryID) values(?, ?, ?, ?, ?)";
        try {
            int rs = JdbcUtil.executeUpdate(sql, name, price, quantity, image, categoryId);
            return rs;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // CẬP NHẬT
    public static int update(Product p) {
        // Đã sửa category_id thành categoryID
        String sql = "UPDATE products SET name=?, price=?, quantity=?, image=?, categoryID=? WHERE id=?";
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

    // Xóa
    public static int delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try {
            return JdbcUtil.executeUpdate(sql, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // LẤY TẤT CẢ
    public static List<Product> findALL() {
        List<Product> list = new ArrayList<>();
        String sql = "select * from products order by id desc";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int price = resultSet.getInt("price");
                
                // Đã sửa category_id thành categoryID
                int categoryId = resultSet.getInt("categoryID"); 
                String image = resultSet.getString("image");

                Product p = new Product(id, name, quantity, price, image, categoryId);
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // LẤY THEO ID
    public static Product findById(int id) {
        Product product = null ;
        String sql = "select * from products where id = ?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, id);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int price = resultSet.getInt("price");
                
                // Đã sửa category_id thành categoryID
                int categoryId = resultSet.getInt("categoryID"); 
                String image = resultSet.getString("image");

                product  = new Product(id, name, quantity, price, image, categoryId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }
}