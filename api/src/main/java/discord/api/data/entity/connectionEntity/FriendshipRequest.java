package discord.api.data.entity.connectionEntity;

import discord.api.data.entity.base.TimeAudit;
import discord.api.data.entity.User;
import discord.api.data.entity.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/**
 * User, User 의 Association Table (사용자 간의 친구 추가 여부)
 */
public class FriendshipRequest extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    @Builder.Default
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @Builder
    public FriendshipRequest(Long id, User requester, User blocked, FriendshipStatus status) {
        this.id = id;
        this.requester = requester;
        this.blocked = blocked;
        this.status = status;
    }
}
