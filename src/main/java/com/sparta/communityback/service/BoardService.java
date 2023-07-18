package com.sparta.communityback.service;

import com.sparta.communityback.dto.*;
import com.sparta.communityback.entity.Board;
import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.repository.BoardReqpository;
import com.sparta.communityback.repository.PostRepository;
import com.sparta.communityback.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final JwtUtil jwtUtil;
    private final BoardReqpository boardReqpository;
    private final UserRepository userRepository;

    //<전체 조회하기>
    public List<BoardResqponseDto> findAll() {
        // db 조회 넘겨주기
        return boardReqpository.findAll()
                .stream()
                .map(BoardResqponseDto::new)
                .toList();
    }

    public BoardResqponseDto createBoard(BoardRequestDto boardRequestDto, User user) {

        checkAuthority_admin(user);
        Board board = new Board(boardRequestDto);
        Board newboard = boardReqpository.save(board);
        return new BoardResqponseDto(newboard);
    }

    @Transactional
    public BoardResqponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto, User user) {
        checkAuthority_admin(user);
        Board board = findBoard(boardId);
        board.update(boardRequestDto);

        return new BoardResqponseDto(board);
    }

    public ResultResponseDto deleteBoard(Long boardId, User user) {
        checkAuthority_admin(user);
        Board board = findBoard(boardId);
        boardReqpository.delete(board);

        return new ResultResponseDto("삭제가 완료 되었습니다.");

    }

    public void checkAuthority_admin(User user) {
        // admin 확인 // 이부분 접근불가 페이지 @Secured로도 가능할 듯
        if (!user.getRole().getAuthority().equals("ROLE_ADMIN")) {
            throw new AuthorizationServiceException("관리자만 가능합니다.");
        }
    }

    private Board findBoard(Long boardId) {
        Board targetBoard = boardReqpository.findById(boardId).orElseThrow(() ->
                new NullPointerException("그런 게시판은 존재하지 않습니다.")
        );
        return targetBoard;
    }


}

