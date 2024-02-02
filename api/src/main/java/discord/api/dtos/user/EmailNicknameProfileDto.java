package discord.api.dtos.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class EmailNicknameProfileDto {
    private String email;
    private String nickname;
    private UUID profileImageId;
    private String profileMsg;
    private String profileImage;

    @Builder
    @QueryProjection
    public EmailNicknameProfileDto(String email, String nickname, UUID profileImageId, String profileMsg) {
        this.email = email;
        this.profileImageId = profileImageId;
        this.nickname = nickname;
        this.profileMsg = profileMsg;
    }

    public void profileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
