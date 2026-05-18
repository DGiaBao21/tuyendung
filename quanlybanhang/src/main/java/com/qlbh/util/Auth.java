/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlbh.util;

import com.qlbh.entity.User;

/**
 *
 * @author PC
 */
public class Auth {
    public static User user = null;
    public static boolean isManage;
    public static void clear(){
        user = null;
    }
    public static boolean isLogin(){
        return Auth.user != null;
    }
    public static boolean isManage(){
        return Auth.isLogin() && user.isRole();
    }
}
