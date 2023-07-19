package com.sparta.communityback.service;

import com.sparta.communityback.dto.CommentRequestDto;
import com.sparta.communityback.dto.CommentResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.Comment;
import com.sparta.communityback.entity.CommentLike;
import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.repository.CommentLikeRepository;
import com.sparta.communityback.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostService PostService;

    public CommentResponseDto createComment(Long boardId, Long postId, CommentRequestDto requestDto, User user) {
        // 해당 게시글이 DB에 존재하는지 확인
        Post targetPost = PostService.findPost(boardId, postId);
        // requestDto를 포함한 comment 저장에 필요한 값들 담아서 주기
        Comment comment = new Comment(requestDto, targetPost, user);
        // DB 저장 넘겨주기
        Comment saveComment = commentRepository.save(comment);
        // Entity -> ResponseDto
        return new CommentResponseDto(saveComment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long boardId, Long postId, Long commentId, CommentRequestDto requestDto, User user) {
        // 댓글 유무 확인
        Comment comment = findComment(boardId, postId, commentId);
        // 권한 확인
        checkAuthority(comment, user);
        // 수정
        comment.update(requestDto);
        // Entity -> ResponseDto
        return new CommentResponseDto(comment);
    }

    public StatusResponseDto deleteComment(Long boardId, Long postId, Long commentId, User user) {
        // 댓글 유무 확인
        Comment comment = findComment(boardId, postId, commentId);
        // 권한 확인
        checkAuthority(comment, user);
        // 삭제
        commentRepository.delete(comment);

        return new StatusResponseDto(HttpStatus.OK.value(), "삭제가 완료 되었습니다.");

    }

    public StatusResponseDto commentLike(Long boardId, Long postId, Long commentId, User user) {
        // 댓글 유무 확인
        Comment comment = findComment(boardId, postId, commentId);
        CommentLike checkCommentLike = commentLikeRepository.findByCommentAndUser(comment, user)
                .orElse(null);
        if (checkCommentLike == null) {
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);
            return new StatusResponseDto(HttpStatus.CREATED.value(), "좋아요 성공.");

        } else {
            commentLikeRepository.delete(checkCommentLike);
            return new StatusResponseDto(HttpStatus.OK.value(), "좋아요 취소.");

        }
    }

    private Comment findComment(Long boardId, Long postId, Long commentId) {
        // 해당 게시글이 DB에 존재하는지 확인
        PostService.findPost(boardId, postId);
        return commentRepository.findByPostPostIdAndCommentId(postId, commentId).orElseThrow(() ->
                new NullPointerException("존재하지 않는 댓글 입니다.")
        );
    }
    // 수정, 삭제시 권한을 확인 .
    public void checkAuthority(Comment comment, User user) {
        // admin 확인
        if(!user.getRole().getAuthority().equals("ROLE_ADMIN")){
            // 작성자 본인 확인
            if (!comment.getUser().getUserId().equals(user.getUserId())) {
                throw new AuthorizationServiceException("작성자만 삭제/수정할 수 있습니다.");
            }
        }
    }
}

