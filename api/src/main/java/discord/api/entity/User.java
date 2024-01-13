package discord.api.entity;

import discord.api.entity.base.TimeAudit;
import discord.api.entity.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private boolean email_authenticated;
    private String password;
    private UUID profile_image;
    private String profile_message;
    private String nickname;
    private LocalDateTime birth;
    private UserStatus userStatus;

    @Builder
    public User(Long id, String password, String email, boolean email_authenticated, UUID profile_image, String profile_message, String nickname, LocalDateTime birth, UserStatus userStatus) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.email_authenticated = email_authenticated;
        this.profile_image = profile_image;
        this.profile_message = profile_message;
        this.nickname = nickname;
        this.birth = birth;
        this.userStatus = userStatus;
    }
}
