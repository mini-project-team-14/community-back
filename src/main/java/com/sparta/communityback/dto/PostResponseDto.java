package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.PostLike;
import com.sparta.communityback.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String createdAt;
    private String modifiedAt;
    private List<String> LikesList;
    private List<CommentResponseDto> comments;


    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = post.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.LikesList = post.getPostLikes()
                .stream()
                .map(PostLike::getUser)
                .map(User::getNickname)
                .toList();
        this.comments = post.getComments()
                .stream()
                .map(CommentResponseDto::new)
                .toList();

    }
}