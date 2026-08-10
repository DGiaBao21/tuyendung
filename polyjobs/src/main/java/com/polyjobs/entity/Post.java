package com.polyjobs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "Posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String content;

    /**
     * Loại bài viết:
     * TIM_VIEC    - Tìm việc (ứng viên)
     * CHIA_SE     - Chia sẻ kinh nghiệm / văn hóa
     * HOI_DAP     - Hỏi đáp phỏng vấn
     * TUYEN_DUNG  - Tuyển dụng gấp (NTD)
     * DU_AN       - Dự án cá nhân (ứng viên)
     * KY_NANG     - Học tập / Kỹ năng
     */
    @Column(nullable = false, length = 50)
    private String type = "CHIA_SE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId", nullable = false)
    private User author;

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Column(nullable = false)
    private Integer commentsCount = 0;

    // Hashtags dạng "java,spring,interview" (lowercase, không dấu #)
    @Column(length = 500)
    private String hashtags;

    // Admin có thể ẩn bài thay vì xóa
    @Column(nullable = false)
    private Boolean isHidden = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}
