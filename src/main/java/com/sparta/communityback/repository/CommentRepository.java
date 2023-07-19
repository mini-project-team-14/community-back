package com.sparta.communityback.repository;

import com.sparta.communityback.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByPostPostIdAndCommentId(Long postId, Long commentId);
}
