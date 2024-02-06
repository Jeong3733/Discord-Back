package discord.api.entity.connectionEntity;

import discord.api.entity.Server;
import discord.api.entity.User;
import discord.api.entity.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)

/**
 * User, Server 의 Association Table
 */
// TODO : User / Server 가 삭제되면 해당 UserServer 도 삭제되도록 cascade 설정 or https://www.inflearn.com/questions/747142 참고
public class UserServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Builder
    public UserServer(Long id, Server server, User user, LocalDateTime createdAt, UserStatus userStatus) {
        this.id = id;
        this.server = server;
        this.user = user;
        this.createdAt = createdAt;
        this.userStatus = userStatus;
    }
}
