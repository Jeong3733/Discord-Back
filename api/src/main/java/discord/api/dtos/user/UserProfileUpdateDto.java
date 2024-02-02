package discord.api.dtos.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateDto {
    private String email;
    private String nickname;
    private String profileMsg;
}
