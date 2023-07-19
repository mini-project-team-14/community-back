package com.sparta.communityback.repository;

import com.sparta.communityback.entity.Board;
import com.sparta.communityback.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByBoardBoardIdAndPostId(Long boardId, Long postId);

    List<Post> findByBoardOrderByCreatedAtDesc(Board board);
}
