package discord.api.entity.dtos.server;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ServerDto {
    private Long id;
    private String name;
    private String description;
    private String profileImage;

    @Builder
    public ServerDto(Long id, String name, String description, String profileImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileImage = profileImage;
    }
}
