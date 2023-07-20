package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Comment;
import com.sparta.communityback.entity.CommentLike;
import com.sparta.communityback.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private String nickname;
    private String createdAt;
    private String modifiedAt;
    private List<String> LikesList; // 형태 바꿔서 줄 생각

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.comment = comment.getComment();
        this.nickname = comment.getUser().getNickname();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = comment.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.LikesList = comment.getCommentLikes()
                .stream()
                .map(CommentLike::getUser)
                .map(User::getNickname)
                .toList();
    }
}