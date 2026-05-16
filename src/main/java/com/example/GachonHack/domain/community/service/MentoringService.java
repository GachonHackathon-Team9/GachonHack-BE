package com.example.GachonHack.domain.community.service;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.community.entity.Post;
import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
import com.example.GachonHack.domain.community.repository.BuddyMatchRequestRepository;
import com.example.GachonHack.domain.community.repository.ChatRoomRepository;
import com.example.GachonHack.domain.community.repository.PostRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentoringService {

    private static final List<BuddyMatchStatus> ACTIVE_STATUSES = List.of(
            BuddyMatchStatus.PENDING,
            BuddyMatchStatus.ACCEPTED
    );

    private final BuddyMatchRequestRepository buddyMatchRequestRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MentoringChatService mentoringChatService;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public CommunityResponseDTO.MentoringRequestCreateResDTO createRequest(
            Long requesterId,
            CommunityRequestDTO.MentoringRequestCreateReqDTO request
    ) {
        User requester = findUser(requesterId);
        User target = findUser(request.targetUserId());
        if (requester.getId().equals(target.getId())) {
            throw new CommunityException(CommunityErrorCode.SELF_MATCH_NOT_ALLOWED);
        }
        if (buddyMatchRequestRepository.existsActiveBetweenUsers(requester, target, ACTIVE_STATUSES)) {
            throw new CommunityException(CommunityErrorCode.DUPLICATE_MATCH_REQUEST);
        }
        BuddyMatchRequest saved = buddyMatchRequestRepository.save(BuddyMatchRequest.builder()
                .requester(requester)
                .target(target)
                .status(BuddyMatchStatus.PENDING)
                .build());
        ChatRoom chatRoom = mentoringChatService.createRoomForMatch(saved);
        return new CommunityResponseDTO.MentoringRequestCreateResDTO(
                saved.getId(),
                chatRoom.getSpace().getId()
        );
    }

    @Transactional
    public CommunityResponseDTO.MentoringRequestCreateResDTO confirmMatchFromPost(
            Long requesterId,
            Long postId,
            CommunityRequestDTO.MentoringMatchFromPostReqDTO request
    ) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
        if (post.getType() != PostType.MENTORING) {
            throw new CommunityException(CommunityErrorCode.POST_NOT_FOUND);
        }
        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new CommunityException(CommunityErrorCode.MATCH_REQUEST_FORBIDDEN);
        }
        return createRequest(requesterId, new CommunityRequestDTO.MentoringRequestCreateReqDTO(
                request.targetUserId(),
                postId
        ));
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO.MentoringRequestDTO> getIncomingRequests(Long userId) {
        User user = findUser(userId);
        return buddyMatchRequestRepository.findByTargetAndStatus(user, BuddyMatchStatus.PENDING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CommunityResponseDTO.MentoringRequestDTO respondToRequest(
            Long userId,
            Long requestId,
            CommunityRequestDTO.MentoringRequestRespondReqDTO request
    ) {
        if (request.status() != BuddyMatchStatus.ACCEPTED && request.status() != BuddyMatchStatus.REJECTED) {
            throw new CommunityException(CommunityErrorCode.INVALID_MATCH_STATUS);
        }
        User user = findUser(userId);
        BuddyMatchRequest matchRequest = buddyMatchRequestRepository.findByIdWithUsers(requestId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.MATCH_REQUEST_NOT_FOUND));
        if (!matchRequest.getTarget().getId().equals(user.getId())) {
            throw new CommunityException(CommunityErrorCode.MATCH_REQUEST_FORBIDDEN);
        }
        if (matchRequest.getStatus() != BuddyMatchStatus.PENDING) {
            throw new CommunityException(CommunityErrorCode.INVALID_MATCH_STATUS);
        }
        matchRequest.respond(request.status());
        return toDto(matchRequest);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO.MentoringRequestDTO> getMyMatches(Long userId) {
        User user = findUser(userId);
        return buddyMatchRequestRepository.findByParticipantAndStatus(user, BuddyMatchStatus.ACCEPTED)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
    }

    private CommunityResponseDTO.MentoringRequestDTO toDto(BuddyMatchRequest request) {
        User requester = request.getRequester();
        User target = request.getTarget();
        Long chatSpaceId = chatRoomRepository.findByBuddyMatchAndActiveTrue(request)
                .map(room -> room.getSpace().getId())
                .orElse(null);
        return new CommunityResponseDTO.MentoringRequestDTO(
                request.getId(),
                requester.getId(),
                requester.getNickname(),
                requester.getRealName(),
                target.getId(),
                target.getNickname(),
                target.getRealName(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getRespondedAt(),
                chatSpaceId
        );
    }
}
