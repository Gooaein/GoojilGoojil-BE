package com.inglo.giggle.service;

import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.response.ApplicantDetailDto;
import com.inglo.giggle.dto.response.OwnerDetailDto;
import com.inglo.giggle.dto.response.UserDetailDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplicantRepository;
import com.inglo.giggle.repository.OwnerRepository;
import com.inglo.giggle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final OwnerRepository ownerRepository;

    public UserDetailDto readUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (user.getRole().name().equals("APPLICANT")) {
            Applicant applicant = applicantRepository.findByUser(user)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
            return ApplicantDetailDto.fromEntity(user, applicant);
        }
        else {
            Owner owner = ownerRepository.findByUser(user)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_OWNER));
            return OwnerDetailDto.fromEntity(user, owner);
        }
    }
}
