package discord.api.entity;

import discord.api.entity.base.TimeAudit;
import discord.api.entity.enums.Role;
import discord.api.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private boolean emailAuthenticated;

    private String password;

    private UUID profile_image;

    private String profile_message;

    private String nickname;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void authenticate() {
        this.emailAuthenticated = true;
    }

    public void login() {
        this.userStatus = UserStatus.ONLINE;
    }

    public void logout() {
        this.userStatus = UserStatus.OFFLINE;
    }

    @Builder
    public User(Long id, String email, String password, UUID profile_image, String profile_message, String nickname, LocalDate birth, UserStatus userStatus, Role role) {
        this.id = id;
        this.email = email;
        this.emailAuthenticated = false;
        this.password = password;
        this.profile_image = profile_image;
        this.profile_message = profile_message;
        this.nickname = nickname;
        this.birth = birth;
        this.userStatus = userStatus;
        this.role = role;
    }
}
