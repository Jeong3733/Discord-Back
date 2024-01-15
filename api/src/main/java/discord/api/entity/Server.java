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
    private UUID profile_image;

    @Builder
    public Server(Long id, String name, String description, UUID profile_image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profile_image = profile_image;
    }
}
