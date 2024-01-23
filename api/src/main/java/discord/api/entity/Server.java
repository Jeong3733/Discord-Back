package discord.api.entity;

import discord.api.entity.base.TimeAudit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Server extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(nullable = true)
    private UUID profileImage;

    @Builder
    public Server(Long id, String name, String description, UUID profileImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileImage = profileImage;
    }
}
