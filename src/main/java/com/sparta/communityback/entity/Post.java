package com.sparta.communityback.entity;

import com.sparta.communityback.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "post")
@NoArgsConstructor
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board")
    private Board board;

    // post 삭제시 comment가 같이 삭제되도록 cascade 추가
    @OrderBy("createdAt desc")

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ColumnDefault("0")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();

    public int getLikeCount(){
        return postLikes.size();
    }
    public void addLike(PostLike postlike) {
        this.postLikes.add(postlike);
    }
    public void decreasedLikeCount(){
        if(!postLikes.isEmpty()){
            postLikes.remove(postLikes.size()-1);
        }
    }

    public Post(PostRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.user = user;
        this.content = requestDto.getContent();
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void connectUser(User user) {
        this.user=user;
    }

    public void removeLike(PostLike postlike){
        this.postLikes.remove(postlike);
    }
}
