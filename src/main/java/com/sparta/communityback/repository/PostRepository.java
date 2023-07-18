package com.sparta.communityback.repository;

import com.sparta.communityback.entity.Board;
import com.sparta.communityback.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoardOrderByCreatedAtDesc(Board board);
}
