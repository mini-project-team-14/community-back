package com.sparta.communityback.repository;

import com.sparta.communityback.entity.Comment;
import com.sparta.communityback.entity.CommentLike;
import com.sparta.communityback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
