package com.sparta.communityback.controller;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.Post;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("//api/boards/{boardId}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    //<전체 조회하기>
    @GetMapping()
    public List<PostResponseDto> getPosts() {
        return postService.findAll();
    }

    //<게시글 작성하기>
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto postRequestDto, HttpServletRequest req)
    {
        String token = authentication(req);
        return postService.createPost(postRequestDto, token);
    }
    private String authentication(HttpServletRequest req) {
//        String tokenValue = jwtUtil.getTokenFromRequest(req);
        String token = jwtUtil.getJwtFromHeader(req);

        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("Token Error");
        }
        return token;
    }
    //<상세 조회하기>
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable("id") Long id ){
        return postService.getSelectedPost(id);
    }
    //<게시글 수정하기>
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable("id") Long id, @RequestBody PostRequestDto requestDto, HttpServletRequest req){
        String token = authentication(req);
        return postService.updatePost(id, requestDto, token);

    }
    //<삭제하기>
    @DeleteMapping("/posts/{postId}")
    public StatusResponseDto deletePost(@PathVariable("id") Long id, @RequestBody PostRequestDto requestDto, HttpServletRequest req){
        String token = authentication(req);
        return postService.deletePost(id, requestDto, token);
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
