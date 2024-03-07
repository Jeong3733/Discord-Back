package discord.api.service;

import discord.api.common.exception.ErrorCode;
import discord.api.common.exception.RestApiException;
import discord.api.common.utils.FileUtils;
import discord.api.dtos.user.UserInfoDto;
import discord.api.dtos.user.UserProfileUpdateDto;
import discord.api.entity.User;
import discord.api.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final AwsService awsService;
    private final FileUtils fileUtils;

    @Transactional
    public UserInfoDto getMyPage(String email) {
        User user = getUserByEmail(email);

        String profileImg = null;
        if (user.getProfile_image() != null) {
            byte[] profileImageByteArray = fileUtils.s3ObjectToByteArray(
                    awsService.downloadMultipartFile(user.getProfile_image())
            );

            profileImg = profileImageByteArray != null ?
                    Base64.getEncoder().encodeToString(profileImageByteArray) : null;
        }

        return UserInfoDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileMsg(user.getProfile_message())
                .profileImage(profileImg)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getUserStatus())
                .build();
    }

    @Transactional
    public void updateMyPage(UserProfileUpdateDto userProfileUpdateDto, MultipartFile userProfileImg, String email) throws IOException {
        User user = getUserByEmail(email);

        // 만약 프로필 이미지가 없으면 업로드하고
        // 있으면 삭제하고 업로드
        UUID uuid = awsService.uploadMultipartFile(userProfileImg);
        if (user.getProfile_image() != null) {
            awsService.deleteMultipartFile(user.getProfile_image());
        }

        user.updateProfile(userProfileUpdateDto.getNickname(), userProfileUpdateDto.getProfileMsg(), uuid);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new RestApiException(ErrorCode.EMAIL_NOT_FOUND);
                });
    }
}
