package com.qlbh.dao;


import com.qlbh.entity.BillDetails;
import com.qlbh.util.JdbcUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.spi.DirStateFactory;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PC
 */
public class BillDetailDAO {
    public static int insert(int billId, int productId, int quantity, int price) {
        String sql = "INSERT INTO bill_details (bill_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try {
            return JdbcUtil.executeUpdate(sql, billId, productId, quantity, price);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // lay dl theo id bill
    public  static List<BillDetails> finfByBillID(int billId){
        List<BillDetails> list = new ArrayList<>();
        String sql = "select * from bill_details where bill_id =?";
        try {
            ResultSet resultSet = JdbcUtil.executeQuery(sql, billId);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int productId = resultSet.getInt("product_id");
                int price = resultSet.getInt("price");
                int quantity = resultSet.getInt("quantity");
                BillDetails billDetaail = new BillDetails(id, price, quantity, productId, billId);
                list.add(billDetaail);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
