package discord.api.common.event;

import discord.api.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
public class OnSignUpCompleteEvent {
    private final User user;
}
