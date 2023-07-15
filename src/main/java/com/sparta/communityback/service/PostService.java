package com.sparta.communityback.service;

import com.sparta.communityback.dto.PostRequestDto;
import com.sparta.communityback.dto.PostResponseDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.Post;
import com.sparta.communityback.entity.PostLike;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.repository.PostLikeRepository;
import com.sparta.communityback.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        Post post = new Post(requestDto, user);
        // DB 저장 넘겨주기
        Post savePost = postRepository.save(post);
        // Entity -> ResponseDto
        return new PostResponseDto(savePost);
    }

    public List<PostResponseDto> getAllPosts() {
        // db 조회 넘겨주기
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public PostResponseDto getSelectedPost(Long id) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
        // Entity -> ResponseDto
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
        // 권한 확인
        checkAuthority(post, user);
        // 수정
        post.update(requestDto);
        // Entity -> ResponseDto
        return new PostResponseDto(post);
    }

    // 수정, 삭제시 권한을 확인 (현재 오버로딩으로 처리중이나 내부 로직이 거의 동일하여 합치는게 좋은가 고민중)
    public StatusResponseDto deletePost(Long id, User user) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findPost(id);
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
