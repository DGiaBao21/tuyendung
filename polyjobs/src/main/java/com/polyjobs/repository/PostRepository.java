package com.polyjobs.repository;

import com.polyjobs.entity.Post;
import com.polyjobs.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Feed cộng đồng — phân trang, chỉ lấy bài chưa bị ẩn
    Page<Post> findByIsHiddenFalseOrderByCreatedDateDesc(Pageable pageable);

    // Lọc theo loại — phân trang
    Page<Post> findByTypeAndIsHiddenFalseOrderByCreatedDateDesc(String type, Pageable pageable);

    // Lọc theo hashtag — phân trang
    @Query("SELECT p FROM Post p WHERE p.isHidden = false AND p.hashtags LIKE %:tag% ORDER BY p.createdDate DESC")
    Page<Post> findByHashtagContaining(@Param("tag") String tag, Pageable pageable);

    // Trending: top bài hot nhất trong 7 ngày gần nhất — phân trang
    @Query("SELECT p FROM Post p WHERE p.isHidden = false AND p.createdDate >= :since ORDER BY (p.likesCount * 2 + p.commentsCount) DESC")
    Page<Post> findTrending(@Param("since") Date since, Pageable pageable);

    // Bài của một người dùng — phân trang (kể cả bị ẩn)
    Page<Post> findByAuthorOrderByCreatedDateDesc(User author, Pageable pageable);

    // Admin: lấy TẤT CẢ bài — phân trang
    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

    // Admin: lọc theo loại — phân trang
    Page<Post> findByTypeOrderByCreatedDateDesc(String type, Pageable pageable);

    // ═══ Không phân trang (dùng cho sidebar) ═══
    List<Post> findByIsHiddenFalseOrderByCreatedDateDesc();
    List<Post> findByTypeAndIsHiddenFalseOrderByCreatedDateDesc(String type);
    List<Post> findByAuthorOrderByCreatedDateDesc(User author);
    List<Post> findAllByOrderByCreatedDateDesc();
    List<Post> findByTypeOrderByCreatedDateDesc(String type);

    // Top hashtags
    @Query(value = "SELECT value FROM Posts CROSS APPLY STRING_SPLIT(hashtags, ',') WHERE is_hidden = 0 AND hashtags IS NOT NULL AND hashtags != '' GROUP BY value ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<String> findTopHashtags();
}
