package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardResqponseDto {
    private Long id;
    private String category;

    public BoardResqponseDto(Board board) {
        this.id = board.getId();
        this.category = board.getCategory();
    }
}
