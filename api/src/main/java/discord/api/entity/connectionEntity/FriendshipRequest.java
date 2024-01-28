package discord.api.entity.connectionEntity;

import discord.api.entity.base.TimeAudit;
import discord.api.entity.User;
import discord.api.entity.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friendship_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
/**
 * User, User 의 Association Table (사용자 간의 친구 추가 여부)
 */
public class FriendshipRequest extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }

    public void decline() {
        this.status = FriendshipStatus.DECLINED;
    }

    public void pending() {
        this.status = FriendshipStatus.PENDING;
    }

    public void block() {
        this.status = FriendshipStatus.BLOCKED;
    }

    public void changeSenderReceiver() {
        User temp = this.sender;
        this.sender = this.receiver;
        this.receiver = temp;
    }
}
