package discord.api.entity.dtos.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class NicknameNProfileIImgDto {
    private String nickname;
    private UUID profileImageId;
    private String profileImage;

    @Builder
    @QueryProjection
    public NicknameNProfileIImgDto(String nickname, UUID profileImageId) {
        this.profileImageId = profileImageId;
        this.nickname = nickname;
    }

    public void profileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
