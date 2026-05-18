/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.entity;

import com.qlbh.dao.ProductDAO;

/**
 *
 * @author PC
 */
public class BillDetails {
    int id;
    int price;
    int quantity;
    int productID;
    int BillID;

    public BillDetails() {
    }

    public BillDetails(int id, int price, int quantity, int productID, int BillID) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.productID = productID;
        this.BillID = BillID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getBillID() {
        return BillID;
    }

    public void setBillID(int BillID) {
        this.BillID = BillID;
    }
    public String getProductName() {
      Product product  = ProductDAO.findById(this.productID);
      return product != null ? product.getName():"";
    }
 
}
