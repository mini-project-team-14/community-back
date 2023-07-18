package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.PostLike;
import com.sparta.communityback.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;
    private List<String> LikesList;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.comments = post.getComments()
                .stream()
                .map(CommentResponseDto::new)
                .toList();
        this.LikesList = post.getPostLikes()
                .stream()
                .map(PostLike::getUser)
                .map(User::getNickname)
                .toList();
    }
}