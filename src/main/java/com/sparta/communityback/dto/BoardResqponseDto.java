package com.sparta.communityback.dto;

import com.sparta.communityback.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardResqponseDto {
    @NotBlank
    private String category;

    public BoardResqponseDto(Board board) {
        this.category = board.getCategory();
    }
}
