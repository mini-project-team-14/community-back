package com.sparta.communityback.controller;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.security.UserDetailsImpl;
import com.sparta.communityback.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //<전체 조회하기>
    @GetMapping()
    public List<PostResponseDto> getPosts(@PathVariable Long boardId) {
        return postService.findAll(boardId);
    }

    //<게시글 작성하기>
    @PostMapping("/posts")
    public PostResponseDto createPost(@PathVariable Long boardId,
                                      @RequestBody @Valid PostRequestDto postRequestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return postService.createPost(postRequestDto, boardId, user);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<StatusResponseDto> postLike(@PathVariable Long boardId,
                                                      @PathVariable Long postId,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        StatusResponseDto statusResponseDto = postService.postLike(boardId, postId, user);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.OK);
    }

    //<상세 조회하기>
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable Long boardId,
                                   @PathVariable Long postId) {
        return postService.getSelectedPost(boardId, postId);
    }

    //<게시글 수정하기>
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable Long boardId,
                                      @PathVariable Long postId,
                                      @RequestBody @Valid PostRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return postService.updatePost(boardId, postId, requestDto, user);

    }

    //<삭제하기>
    @DeleteMapping("/posts/{postId}")
    public StatusResponseDto deletePost(@PathVariable Long boardId,
                                        @PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return postService.deletePost(boardId, postId, user);
    }

}
