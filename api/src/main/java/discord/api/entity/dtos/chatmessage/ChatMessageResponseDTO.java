package discord.api.entity.dtos.chatmessage;

import com.fasterxml.jackson.annotation.JsonFormat;
import discord.api.entity.document.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChatMessageResponseDTO {

    private long id;
    private long userId;
    private String nickname;
    private String imageCode;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private boolean isUpdated;

    public static ChatMessageResponseDTO toDTO(final ChatMessage chatMessage, final String nickname, final String imageCode) {
        final ChatMessageResponseDTO chatMessageResponseDTO = ChatMessageResponseDTO.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .nickname(nickname)
                .imageCode(imageCode)
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .build();

        chatMessageResponseDTO.setIsUpdated(chatMessage.getUpdatedAt());

        return chatMessageResponseDTO;
    }

    public static ChatMessageResponseDTO toDTO(final ChatMessage chatMessage, final String imageCode) {
        final ChatMessageResponseDTO chatMessageResponseDTO = ChatMessageResponseDTO.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .imageCode(imageCode)
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .build();

        chatMessageResponseDTO.setIsUpdated(chatMessage.getUpdatedAt());

        return chatMessageResponseDTO;
    }

    public void setIsUpdated(final LocalDateTime updatedAt) {
        if(updatedAt != null && updatedAt.isAfter(createdAt)) {
            this.isUpdated = true;
        }
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
}
