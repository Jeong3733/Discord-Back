package discord.api.service;

import discord.api.entity.document.ChatMessage;
import discord.api.entity.dtos.chatmessage.ChatMessageResponseDTO;
import discord.api.repository.User.UserRepository;
import discord.api.repository.chatmessage.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageFacadeService {

    private static final int CHAT_MESSAGE_LIMIT = 300;

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AwsService awsService;

    public Slice<ChatMessageResponseDTO> getChatMessages(final long roomId) {
        final Slice<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, createPageRequest());
        return getChatMessageResponseDTOS(roomId, chatMessages);
    }

    public Slice<ChatMessageResponseDTO> getMoreChatMessages(final long roomId, final long lastChatId) {
        final Slice<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(roomId, lastChatId, createPageRequest());
        return getChatMessageResponseDTOS(roomId, chatMessages);
    }

    private Slice<ChatMessageResponseDTO> getChatMessageResponseDTOS(final long roomId, final Slice<ChatMessage> chatMessages) {
        final Map<Long, String> userMap = userRepository.getUserListByServerId(roomId).stream().collect(Collectors.toMap(
                user -> (long) user[0],
                user -> (String) user[1]
        ));

        return chatMessages.map(chatMessage -> {
            String imageCode = null;
            if (chatMessage.getFileName() != null) {
                imageCode = awsService.downloadFile(chatMessage.getFileName());
            }
            return ChatMessageResponseDTO.toDTO(chatMessage,  userMap.get(chatMessage.getUserId()), imageCode);
        });
    }


    private PageRequest createPageRequest() {
        return PageRequest.ofSize(CHAT_MESSAGE_LIMIT);
    }

}
