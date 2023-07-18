package com.sparta.communityback.entity;

import com.sparta.communityback.dto.BoardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "board")
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    public Board(BoardRequestDto requestDto) {
        this.category = requestDto.getCategory();
    }

    public void update(BoardRequestDto boardRequestDto) {
        this.category = boardRequestDto.getCategory();
    }
}
