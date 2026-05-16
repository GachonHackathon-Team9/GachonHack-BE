package com.example.GachonHack.domain.community.service;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
import com.example.GachonHack.domain.community.repository.PostRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final short FRESHMAN_GRADE = 1;

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO.PostSummaryDTO> getPosts(PostType type, Long userId) {
        List<Post> posts = type == null
                ? postRepository.findAllWithAuthor()
                : postRepository.findByTypeWithAuthor(type);
        return posts.stream()
                .map(post -> toSummary(post, false))
                .toList();
    }

    @Transactional
    public CommunityResponseDTO.PostCreateResDTO createPost(Long userId, CommunityRequestDTO.PostCreateReqDTO request) {
        User author = findUser(userId);
        if (request.type() == PostType.MENTORING && !isFreshman(author)) {
            throw new CommunityException(CommunityErrorCode.FRESHMAN_ONLY_POST);
        }
        Post post = Post.builder()
                .author(author)
                .title(request.title())
                .body(request.body())
                .type(request.type())
                .build();
        Post saved = postRepository.save(post);
        return new CommunityResponseDTO.PostCreateResDTO(saved.getId());
    }

    @Transactional
    public CommunityResponseDTO.PostDetailDTO getPostDetail(Long postId, Long userId) {
        if (postRepository.incrementViewCount(postId) == 0) {
            throw new CommunityException(CommunityErrorCode.POST_NOT_FOUND);
        }
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        return toDetail(post, Collections.emptyList(), false);
    }

    private boolean isFreshman(User user) {
        return user.getGrade() != null && user.getGrade() == FRESHMAN_GRADE;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
    }

    private CommunityResponseDTO.PostSummaryDTO toSummary(Post post, boolean isLiked) {
        User author = post.getAuthor();
        return new CommunityResponseDTO.PostSummaryDTO(
                post.getId(),
                post.getTitle(),
                author.getNickname(),
                author.getRealName(),
                post.getType(),
                post.getViewCount(),
                post.getLikeCount(),
                isLiked,
                post.getCreatedAt()
        );
    }

    private CommunityResponseDTO.PostDetailDTO toDetail(
            Post post,
            List<CommunityResponseDTO.CommentDTO> comments,
            boolean isLiked
    ) {
        User author = post.getAuthor();
        return new CommunityResponseDTO.PostDetailDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                author.getNickname(),
                author.getRealName(),
                post.getType(),
                post.getViewCount(),
                post.getLikeCount(),
                isLiked,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                comments
        );
    }
}
