package com.sparta.communityback.controller;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.security.UserDetailsImpl;
import com.sparta.communityback.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtil jwtUtil;

    //<전체 조회하기>
    @GetMapping()
    public List<PostResponseDto> getPosts() {
        return postService.findAll();
    }

    //<게시글 작성하기>
    @PostMapping("/posts")
    public PostResponseDto createPost(@PathVariable Long boardId,
                                      @RequestBody PostRequestDto postRequestDto,
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

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<StatusResponseDto> postlike(@PathVariable Long postId,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        StatusResponseDto statusResponseDto = postService.postLike(postId, user);
        return new ResponseEntity<>(statusResponseDto, HttpStatus.OK);
    }


    //<상세 조회하기>
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") Long id ){
        return postService.getSelectedPost(id);
    }

    //<게시글 수정하기>
    @PutMapping("/posts/{postId}")
    public PostResponseDto updatePost(@PathVariable("postId") Long id, @RequestBody PostRequestDto requestDto, HttpServletRequest req){
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
