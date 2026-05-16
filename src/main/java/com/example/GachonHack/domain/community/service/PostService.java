package com.example.GachonHack.domain.community.service;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.entity.PostComment;
import com.example.GachonHack.domain.community.entity.PostLike;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
import com.example.GachonHack.domain.community.repository.PostCommentRepository;
import com.example.GachonHack.domain.community.repository.PostLikeRepository;
import com.example.GachonHack.domain.community.repository.PostRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final short FRESHMAN_GRADE = 1;

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO.PostSummaryDTO> getPosts(PostType type) {
        List<Post> posts = type == null
                ? postRepository.findAllWithAuthor()
                : postRepository.findByTypeWithAuthor(type);
        return posts.stream().map(this::toSummary).toList();
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
    public CommunityResponseDTO.PostDetailDTO getPostDetail(Long postId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();
        List<CommunityResponseDTO.CommentDTO> comments = postCommentRepository.findByPostWithAuthor(post)
                .stream()
                .map(this::toComment)
                .toList();
        return toDetail(post, comments);
    }

    @Transactional
    public CommunityResponseDTO.CommentCreateResDTO createComment(
            Long userId,
            Long postId,
            CommunityRequestDTO.CommentCreateReqDTO request
    ) {
        User author = findUser(userId);
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        validateCommentPermission(author, post);
        PostComment comment = PostComment.builder()
                .post(post)
                .author(author)
                .body(request.body())
                .build();
        PostComment saved = postCommentRepository.save(comment);
        return new CommunityResponseDTO.CommentCreateResDTO(saved.getId());
    }

    @Transactional
    public CommunityResponseDTO.PostLikeResDTO likePost(Long userId, Long postId) {
        User user = findUser(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new CommunityException(CommunityErrorCode.ALREADY_LIKED);
        }
        postLikeRepository.save(PostLike.builder().post(post).user(user).build());
        post.increaseLikeCount();
        return new CommunityResponseDTO.PostLikeResDTO(post.getId(), post.getLikeCount());
    }

    private void validateCommentPermission(User author, Post post) {
        if (post.getType() != PostType.MENTORING) {
            return;
        }
        if (isSenior(author)) {
            if (post.getAuthor().getId().equals(author.getId())) {
                throw new CommunityException(CommunityErrorCode.COMMENT_NOT_ALLOWED);
            }
            return;
        }
        if (isFreshman(author) && post.getAuthor().getId().equals(author.getId())) {
            return;
        }
        throw new CommunityException(CommunityErrorCode.COMMENT_NOT_ALLOWED);
    }

    private boolean isFreshman(User user) {
        return user.getGrade() != null && user.getGrade() == FRESHMAN_GRADE;
    }

    private boolean isSenior(User user) {
        return user.getGrade() != null && user.getGrade() > FRESHMAN_GRADE;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
    }

    private CommunityResponseDTO.PostSummaryDTO toSummary(Post post) {
        User author = post.getAuthor();
        return new CommunityResponseDTO.PostSummaryDTO(
                post.getId(),
                post.getTitle(),
                author.getNickname(),
                author.getRealName(),
                post.getType(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedAt()
        );
    }

    private CommunityResponseDTO.PostDetailDTO toDetail(Post post, List<CommunityResponseDTO.CommentDTO> comments) {
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
                post.getCreatedAt(),
                post.getUpdatedAt(),
                comments
        );
    }

    private CommunityResponseDTO.CommentDTO toComment(PostComment comment) {
        User author = comment.getAuthor();
        return new CommunityResponseDTO.CommentDTO(
                comment.getId(),
                author.getNickname(),
                author.getRealName(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}
