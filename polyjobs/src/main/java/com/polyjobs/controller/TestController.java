package com.polyjobs.controller;

import com.polyjobs.entity.User;
import com.polyjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test-users")
    public String testUsers() {
        List<User> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder();
        for (User u : users) {
            sb.append("ID: ").append(u.getId()).append("<br/>");
            sb.append("Username: '").append(u.getUsername()).append("' (Len: ").append(u.getUsername() != null ? u.getUsername().length() : 0).append(")<br/>");
            sb.append("Password: '").append(u.getPassword()).append("' (Len: ").append(u.getPassword() != null ? u.getPassword().length() : 0).append(")<br/>");
            
            // Try to find
            User found = userRepository.findByUsernameAndPassword(u.getUsername(), u.getPassword());
            sb.append("Found by exact match: ").append(found != null).append("<br/>");
            
            User foundTrim = userRepository.findByUsernameAndPassword(u.getUsername().trim(), u.getPassword().trim());
            sb.append("Found by trimmed match: ").append(foundTrim != null).append("<br/><hr/>");
        }
        return sb.toString();
    }
}
