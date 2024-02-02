package discord.api.controller;

import discord.api.common.utils.AuthUtils;
import discord.api.dtos.user.UserInfoDto;
import discord.api.dtos.user.UserProfileUpdateDto;
import discord.api.entity.User;
import discord.api.service.MyPageService;
import discord.api.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    private final AuthUtils authUtils;

    @GetMapping("/mypage")
    public ResponseEntity<UserInfoDto> getMyPage(Authentication authentication) {
        User user = authUtils.getUserFromAuthentication(authentication);
        UserInfoDto myPage = myPageService.getMyPage(user.getEmail());

        return ResponseEntity
                .ok()
                .body(myPage);
    }

    @PutMapping("/update/mypage")
    public ResponseEntity<?> updateMyPage(
            @RequestPart("userProfileImage") @Nullable final MultipartFile userProfileImage,
            @RequestPart("updateProfile") UserProfileUpdateDto userProfileUpdateDto,
            Authentication authentication
    ) throws IOException {
        User user = authUtils.getUserFromAuthentication(authentication);
        myPageService.updateMyPage(userProfileUpdateDto, userProfileImage, user.getEmail());
        return ResponseEntity
                .ok()
                .build();
    }
}
