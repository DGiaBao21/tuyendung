package com.polyjobs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Dùng toUri().toString() để đảm bảo đúng cú pháp file:// trên cả Windows lẫn Linux
            String resourceLocation = uploadPath.toUri().toString();
            if (!resourceLocation.endsWith("/")) {
                resourceLocation += "/";
            }

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(resourceLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
