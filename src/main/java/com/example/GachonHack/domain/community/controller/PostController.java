package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.community.exception.code.CommunitySuccessCode;
import com.example.GachonHack.domain.community.service.PostService;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/posts", "/posts"})
public class PostController implements PostControllerDocs {

    private final PostService postService;

    @Override
    @GetMapping
    public ApiResponse<List<CommunityResponseDTO.PostSummaryDTO>> getPosts(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(required = false) PostType type
    ) {
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_LIST_SUCCESS, postService.getPosts(type, user.getId()));
    }

    @Override
    @PostMapping
    public ApiResponse<CommunityResponseDTO.PostCreateResDTO> createPost(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody CommunityRequestDTO.PostCreateReqDTO request
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.POST_CREATE_SUCCESS,
                postService.createPost(user.getId(), request)
        );
    }

    @Override
    @GetMapping("/{postId}")
    public ApiResponse<CommunityResponseDTO.PostDetailDTO> getPost(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long postId
    ) {
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_DETAIL_SUCCESS, postService.getPostDetail(postId, user.getId()));
    }

    @Override
    @PostMapping("/{postId}/comments")
    public ApiResponse<CommunityResponseDTO.CommentCreateResDTO> createComment(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long postId,
            @Valid @RequestBody CommunityRequestDTO.CommentCreateReqDTO request
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.COMMENT_CREATE_SUCCESS,
                postService.createComment(user.getId(), postId, request)
        );
    }

    @Override
    @PostMapping("/{postId}/like")
    public ApiResponse<CommunityResponseDTO.PostLikeResDTO> likePost(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long postId
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.POST_LIKE_SUCCESS,
                postService.likePost(user.getId(), postId)
        );
    }
}
