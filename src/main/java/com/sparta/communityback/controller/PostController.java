package com.sparta.communityback.controller;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    //<전체 조회하기>
    @GetMapping()
    public List<PostResponseDto> getPosts(@PathVariable Long boardId) {
        return postService.findAll(boardId);
    }

    //<게시글 작성하기>
    @PostMapping("/posts")
    public PostResponseDto createPost(@PathVariable Long boardId,
                                      @RequestBody @Valid PostRequestDto postRequestDto,
                                      HttpServletRequest req) {
        String token = authentication(req);
        return postService.createPost(postRequestDto, boardId, token);
    }
    private String authentication(HttpServletRequest req) {
//        String token = jwtUtil.getJwtFromHeader(req);
        String token = jwtUtil.getTokenFromRequest(req);
        token = jwtUtil.substringToken(token);
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("Token Error");
        }
        return token;
    }

    // <게시글 좋아요 추가 >
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> addLike(@PathVariable("postId") Long postId) {
        postService.addLikeToPost(postId);
        return ResponseEntity.ok().build();
    }

    // <게시글 좋아요 취소 >
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> removeLike(@PathVariable("postId") Long postId) {
        postService.removeLikeFromPost(postId);
        return ResponseEntity.ok().build();
    }

    //<상세 조회하기>
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") Long id ){
        return postService.getSelectedPost(id);
    }

    //<게시글 수정하기>
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable("postId") Long id, @RequestBody @Valid PostRequestDto requestDto, HttpServletRequest req){
        String token = authentication(req);
        return postService.updatePost(id, requestDto, token);

    }
    //<삭제하기>
    @DeleteMapping("/posts/{postId}")
    public StatusResponseDto deletePost(@PathVariable("postId") Long id, HttpServletRequest req){
        String token = authentication(req);
        return postService.deletePost(id, token);
    }

}

//    // @CookieValue 가 아니라 헤더로 받아올 것
//    @PostMapping("/posts")
//    public PostResponseDto createPost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @RequestBody PostRequestDto requestDto) {
//        return postService.createPost(tokenValue, requestDto);
//    }
//
//    @PutMapping("/posts/{id}")
//    public PostResponseDto updatePost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
//        return postService.updatePost(tokenValue, id, requestDto);
//    }
//
//    @DeleteMapping("/posts/{id}")
//    public ResponseEntity<MessageResponseDto> deletePost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id) {
//        return postService.deletePost(tokenValue, id);
//    }
//}
