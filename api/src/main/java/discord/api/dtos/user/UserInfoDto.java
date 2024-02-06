package discord.api.dtos.user;

import discord.api.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class UserInfoDto {
    private String email;
    private String nickname;
    private String profileImage;
    private String profileMsg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserStatus status;

    @Builder
    public UserInfoDto(String email, String nickname, String profileImage, String profileMsg, LocalDateTime createdAt, LocalDateTime updatedAt, UserStatus status) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.profileMsg = profileMsg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }
}
