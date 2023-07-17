package com.sparta.communityback.service;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.Board;
import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.PostLike;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.repository.BoardReqpository;
import com.sparta.communityback.repository.PostLikeRepository;
import com.sparta.communityback.repository.PostRepository;
import com.sparta.communityback.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardReqpository boardReqpository;
    private final PostLikeRepository postLikeRepository;
    private final JwtUtil jwtUtil;

    //<전체 조회하기>
    public List<PostResponseDto> findAll() {
        // db 조회 넘겨주기
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponseDto::new)
                .toList();
    }

    //<게시글 작성하기>
    public PostResponseDto createPost(PostRequestDto requestDto, Long boardId, String token) {
        Board board = boardReqpository.findById(boardId).orElseThrow(() ->
                new NullPointerException("해당 게시판은 존재하지 않습니다")
        );
        String username = getUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new IllegalArgumentException("해당 유저는 존재하지 않습니다"));
        Post post = new Post(requestDto, board, user);
//        post.connectUser(user);
        Post savePost = postRepository.save(post);
        return new PostResponseDto(savePost);
    }

    //<게시글 좋아요 추가>
    public void addLikeToPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getPostLikes().isEmpty()) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
        post.addLike(new PostLike());
        postRepository.save(post);
    }
    //<게시글 좋아요 취소>
    public void removeLikeFromPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("이미 좋아요를 취소했습니다"));
        post.decreasedLikeCount();
        postRepository.save(post);
    }

    //payload에 들어갈 정보들이 claim
    private String getUsername(String token) {
        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        return username;
    }

    //<상세 조회하기>
    public PostResponseDto getSelectedPost(Long id) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
        // Entity -> ResponseDto
        return new PostResponseDto(post);
    }
    //<게시글 수정하기>
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, String token) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
        String username = getUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new IllegalArgumentException("해당 유저는 존재하지 않습니다"));
        // 권한 확인
        checkAuthority(post, user);
        // 수정
        post.update(requestDto);
        // Entity -> ResponseDto
        return new PostResponseDto(post);
    }

    //<삭제하기>
    public StatusResponseDto deletePost(Long id, String token) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
        String username = getUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new IllegalArgumentException("해당 유저는 존재하지 않습니다"));
        // 권한 확인
        checkAuthority(post, user);
        // 삭제
        postRepository.delete(post);

        return new StatusResponseDto(HttpStatus.OK.value(), "삭제가 완료 되었습니다.");

    }

    protected Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 게시물 입니다.")
        );
    }

    public void checkAuthority(Post post, User user) {
        // admin 확인
        if (!user.getRole().getAuthority().equals("ROLE_ADMIN")) {
            // username만 확인하는 것 보다 이쪽이 더 안전하다고 생각하여 작성하였으나 true가 나오지 않음.
//            if (!post.getUser().equals(user)) {
            if (post.getUser().getId() != user.getId()) {
                throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
            }
        }
    }
}
