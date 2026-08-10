package com.polyjobs.repository;

import com.polyjobs.entity.Post;
import com.polyjobs.entity.PostComment;
import com.polyjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    List<PostComment> findByPostOrderByCreatedDateAsc(Post post);

    long countByPost(Post post);

    List<PostComment> findByAuthor(User author);
}
