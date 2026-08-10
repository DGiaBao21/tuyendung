package com.polyjobs.repository;

import com.polyjobs.entity.Post;
import com.polyjobs.entity.PostLike;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);
}
