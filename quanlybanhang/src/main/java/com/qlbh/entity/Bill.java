/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.entity;

import java.util.Date;

/**
 *
 * @author PC
 */
public class Bill {
   private int id;
    private Date createdDate;
    private int total;
    private String status;
    private int customerID;   // <-- sửa tên cho đúng
    private int userID;
    private String mahd;

    public Bill() {
    }

    public Bill(int id, Date createdDate, int total, String status, int customerID, int userID, String mahd) {
        this.id = id;
        this.createdDate = createdDate;
        this.total = total;
        this.status = status;
        this.customerID = customerID;
        this.userID = userID;
        this.mahd = mahd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMahd() {
        return mahd;
    }

    public void setMahd(String mahd) {
        this.mahd = mahd;
    }

   
 
  
}
