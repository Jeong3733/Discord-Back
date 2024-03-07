package discord.api.controller;

import discord.api.entity.dtos.chatmessage.ChatMessageResponseDTO;
import discord.api.service.ChatMessageFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageFacadeService chatService;

    @GetMapping("/server/{serverId}")
    public ResponseEntity<Slice<ChatMessageResponseDTO>> getChatMessages(@PathVariable final long serverId) {
       return ResponseEntity.ok(chatService.getChatMessages(serverId));
    }

    @GetMapping("/server/{serverId}/chat/{lastChatId}")
    public ResponseEntity<Slice<ChatMessageResponseDTO>> getMoreChatMessages(@PathVariable(value = "serverId") final long serverId,
                                                                             @PathVariable(value = "lastChatId") final long lastChatId) {
        return ResponseEntity.ok(chatService.getMoreChatMessages(serverId, lastChatId));
    }
}
