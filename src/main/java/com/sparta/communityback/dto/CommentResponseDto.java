package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comments;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer countLikes; // 형태 바꿔서 줄 생각

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.comments = comment.getComment();
        this.username = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.countLikes = comment.getCommentLikes().size();
    }
}