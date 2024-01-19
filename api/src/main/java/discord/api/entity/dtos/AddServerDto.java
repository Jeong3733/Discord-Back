package discord.api.entity.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class AddServerDto {
    private String name;
    private String description;
}
