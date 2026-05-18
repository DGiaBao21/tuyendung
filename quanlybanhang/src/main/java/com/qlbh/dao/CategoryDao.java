/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.dao;

import com.qlbh.entity.Category;
import com.qlbh.util.JdbcUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class CategoryDao {
      // them
    public static int insert(Category category){
        String  sql = "insert into caterories values(?)";
        try{
             int rs = JdbcUtil.executeUpdate(sql, category.getName());
             return rs;
        }catch(Exception ex){
            ex.printStackTrace();
        }
         return 0;
    }
    
     public static int insert(String name){
        String  sql = "insert into categories values(?)";
        try{
             int rs = JdbcUtil.executeUpdate(sql, name);
             return rs;
        }catch(Exception ex){
            ex.printStackTrace();
        }
         return 0;
    }
     // sua
      public static int update(int id, String name){
        String  sql = "update categories set name = ? where id = ?";
        try{
             int rs = JdbcUtil.executeUpdate(sql, name,id);
             return rs;
        }catch(Exception ex){
            ex.printStackTrace();
        }
         return 0;
    }
     
     
     
     // lay
 public static List<Category> findALL(){
     List<Category> list = new ArrayList<>();
     String sql = "select * from categories order by id desc";
     try { 
         ResultSet resultSet = JdbcUtil.executeQuery(sql);
         while(resultSet.next()){
             int id = resultSet.getInt("id");
             String name = resultSet.getString("name");
             Category cate = new Category(id,name);
             list.add(cate);
         } 
     }catch (Exception e) {
         e.printStackTrace();
     }
    return list;
}
 //xóa
public static int delete(int id){
    String sql = "DELETE FROM categories WHERE id = ?";
    try{
        int rs = JdbcUtil.executeUpdate(sql, id);
        return rs;
    }catch(Exception ex){
        ex.printStackTrace();
    }
    return 0;
}
//lam mới
public static int deleteAll(){
    String sql = "DELETE FROM categories";  
    try{
        return JdbcUtil.executeUpdate(sql);
    }catch(Exception ex){
        ex.printStackTrace();
    }
    return 0;
}



}


