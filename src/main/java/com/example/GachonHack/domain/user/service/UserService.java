package com.example.GachonHack.domain.user.service;

import com.example.GachonHack.domain.user.dto.req.UserRequestDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.exception.UserException;
import com.example.GachonHack.domain.user.exception.code.UserErrorCode;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void createUserOnboarding(Long userId, UserRequestDTO.OnboardingReqDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        String nickname = generateUniqueNickname();
        user.completeProfile(request.real_name(), request.student_id(), request.grade(), nickname);
    }

    private String generateUniqueNickname() {
        String nickname;
        do {
            nickname = String.format("%06d", random.nextInt(1_000_000));
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }
}
