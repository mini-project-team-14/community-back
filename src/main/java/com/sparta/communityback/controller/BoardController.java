package com.sparta.communityback.controller;

import com.sparta.communityback.dto.BoardRequestDto;
import com.sparta.communityback.dto.BoardResqponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.security.UserDetailsImpl;
import com.sparta.communityback.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping()
    public List<BoardResqponseDto> getBoards() {
        return boardService.findAll();
    }

    @PostMapping()
    public ResponseEntity<BoardResqponseDto> createBoard(@RequestBody @Valid BoardRequestDto boardRequestDto,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok()
                .body(boardService.createBoard(boardRequestDto, user));
    }

    @PutMapping("{boardId}")
    public ResponseEntity<BoardResqponseDto> updateBoard(@PathVariable Long boardId,
                                                         @RequestBody @Valid BoardRequestDto boardRequestDto,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok()
                .body(boardService.updateBoard(boardId, boardRequestDto, user));
    }

    @DeleteMapping("{boardId}")
    public ResponseEntity<StatusResponseDto> deleteBoard(@PathVariable Long boardId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok()
                .body(boardService.deleteBoard(boardId, user));
    }

}
