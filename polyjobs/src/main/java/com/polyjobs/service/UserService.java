package com.polyjobs.service;

import com.polyjobs.dto.RegisterDTO;
import com.polyjobs.dto.UserDTO;
import com.polyjobs.entity.Company;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Convert Entity to DTO
    public UserDTO convertToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setAvatar(user.getAvatar());
        dto.setProfession(user.getProfession());
        dto.setRole(user.getRole());
        dto.setIsAdmin(user.getIsAdmin());
        dto.setIsActive(user.getIsActive());
        return dto;
    }

    // Convert List Entity to List DTO
    public List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO findByUsername(String username) {
        return convertToDTO(userRepository.findByUsername(username));
    }
    
    public UserDTO findById(Integer id) {
        return convertToDTO(userRepository.findById(id).orElse(null));
    }

    public User findEntityById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDTO registerUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFullname(registerDTO.getFullname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole());

        user = userRepository.save(user);

        // Tạo công ty nếu là nhà tuyển dụng
        if (Boolean.TRUE.equals(registerDTO.getRole()) && registerDTO.getCompanyName() != null && !registerDTO.getCompanyName().trim().isEmpty()) {
            Company company = new Company();
            company.setCompanyName(registerDTO.getCompanyName().trim());
            company.setAddress(registerDTO.getCompanyAddress() != null ? registerDTO.getCompanyAddress().trim() : "Đang cập nhật");
            company.setWebsite(registerDTO.getCompanyWebsite() != null ? registerDTO.getCompanyWebsite().trim() : "");
            company.setDescription("Doanh nghiệp " + registerDTO.getCompanyName().trim() + " hoạt động tại miền Tây Nam Bộ.");
            company.setEmployer(user);
            companyRepository.save(company);
        }

        return convertToDTO(user);
    }
}
