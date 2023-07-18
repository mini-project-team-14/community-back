package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Comment;
import com.sparta.communityback.entity.CommentLike;
import com.sparta.communityback.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<String> LikesList; // 형태 바꿔서 줄 생각

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.LikesList = comment.getCommentLikes()
                .stream()
                .map(CommentLike::getUser)
                .map(User::getNickname)
                .toList();
    }
}