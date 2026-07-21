package com.polyjobs.config;

import com.polyjobs.entity.Category;
import com.polyjobs.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Autowired
    private CategoryRepository categoryRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // Seed Categories if empty
            if (categoryRepository.count() == 0) {
                List<Category> initialCategories = Arrays.asList(
                        createCategory("Công nghệ thông tin / IT", "Lập trình, hệ thống, mạng, phân tích dữ liệu..."),
                        createCategory("Kinh doanh / Bán hàng", "Nhân viên kinh doanh, Sale, quản lý cửa hàng..."),
                        createCategory("Kế toán / Kiểm toán", "Kế toán tổng hợp, kế toán viên, kiểm toán..."),
                        createCategory("Marketing / PR", "Chuyên viên truyền thông, quảng cáo, SEO, Content..."),
                        createCategory("Hành chính / Nhân sự", "Tuyển dụng, đào tạo, văn thư lưu trữ..."),
                        createCategory("Thiết kế / Mỹ thuật", "Thiết kế đồ họa, UI/UX, kiến trúc...")
                );
                categoryRepository.saveAll(initialCategories);
            }
        };
    }

    private Category createCategory(String name, String desc) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(desc);
        return c;
    }
}
