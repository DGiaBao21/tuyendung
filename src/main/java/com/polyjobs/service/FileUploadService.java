package com.polyjobs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    /**
     * Lưu file upload vào thư mục ngoài (external directory)
     * @param file file được upload
     * @param subDir thư mục con (ví dụ: "avatars", "cv", "company")
     * @return đường dẫn tương đối URL (ví dụ: "/uploads/avatars/abc.png")
     */
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path targetDir = Paths.get(uploadDir, subDir);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;
        Path targetPath = targetDir.resolve(newFilename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + subDir + "/" + newFilename;
    }

    /**
     * Xóa file upload từ thư mục ngoài dựa theo URL
     * @param fileUrl đường dẫn URL (ví dụ: "/uploads/cv/abc.pdf")
     * @return true nếu xóa thành công, false nếu lỗi/không tồn tại
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return false;
        }
        try {
            String relativePath = fileUrl.substring("/uploads/".length());
            Path targetPath = Paths.get(uploadDir, relativePath);
            return Files.deleteIfExists(targetPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
