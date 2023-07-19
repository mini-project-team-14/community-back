package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardResqponseDto {
    private Long BoardId;
    private String category;

    public BoardResqponseDto(Board board) {
        this.BoardId = board.getBoardId();
        this.category = board.getCategory();
    }
}
