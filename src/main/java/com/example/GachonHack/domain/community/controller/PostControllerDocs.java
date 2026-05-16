package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Post", description = "게시판 관련 API")
public interface PostControllerDocs {

    @Operation(summary = "게시글 목록 조회 API", description = "게시글 유형(type)으로 필터링하여 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<List<CommunityResponseDTO.PostSummaryDTO>> getPosts(
            @RequestParam(required = false) PostType type
    );

    @Operation(summary = "게시글 작성 API", description = "꿀팁·짝선짝후 등 게시글을 작성합니다. MENTORING 유형은 새내기(1학년)만 작성 가능합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "작성 권한 없음")
    })
    ApiResponse<CommunityResponseDTO.PostCreateResDTO> createPost(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommunityRequestDTO.PostCreateReqDTO request
    );

    @Operation(summary = "게시글 상세 조회 API", description = "게시글 상세와 댓글 목록을 반환합니다. 조회 시 조회수가 1 증가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    ApiResponse<CommunityResponseDTO.PostDetailDTO> getPost(@PathVariable Long postId);

    @Operation(summary = "댓글 작성 API", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "작성 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    ApiResponse<CommunityResponseDTO.CommentCreateResDTO> createComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @Valid @RequestBody CommunityRequestDTO.CommentCreateReqDTO request
    );

    @Operation(summary = "게시글 추천 API", description = "게시글에 추천(좋아요)을 등록합니다. 중복 추천은 불가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추천 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 추천함"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    ApiResponse<CommunityResponseDTO.PostLikeResDTO> likePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId
    );
}
