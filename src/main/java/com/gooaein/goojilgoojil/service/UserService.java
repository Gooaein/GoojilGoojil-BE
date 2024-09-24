package com.gooaein.goojilgoojil.service;

import com.gooaein.goojilgoojil.domain.User;
import com.gooaein.goojilgoojil.dto.response.UserDetailDto;
import com.gooaein.goojilgoojil.exception.CommonException;
import com.gooaein.goojilgoojil.exception.ErrorCode;
import com.gooaein.goojilgoojil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailDto readUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return UserDetailDto.fromEntity(user);
    }
}
