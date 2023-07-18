package com.sparta.communityback.controller;

import com.sparta.communityback.dto.CommentRequestDto;
import com.sparta.communityback.dto.CommentResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.security.UserDetailsImpl;
import com.sparta.communityback.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards/{boardId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId,
                                                           @RequestBody @Valid CommentRequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.createComment(postId, requestDto, user), HttpStatus.OK);
    }

    @PutMapping("{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId,
                                                            @RequestBody @Valid CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.updateComment(commentId, requestDto, user), HttpStatus.OK);
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<StatusResponseDto> deleteCommen(@PathVariable Long commentId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new ResponseEntity<>(commentService.deleteComment(commentId, user), HttpStatus.OK);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<StatusResponseDto> commentLike(@PathVariable Long commentId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        StatusResponseDto statusCodesResponseDto = commentService.commentLike(commentId, user);
          return new ResponseEntity<>(statusCodesResponseDto, HttpStatus.OK);
    }


}
