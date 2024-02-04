package com.hansori.ws.stomp.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hansori.ws.db.mongo.document.ChatMessage;
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
    private String imageURL;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    private boolean isUpdated;

    public static ChatMessageResponseDTO toDTO(final ChatMessage chatMessage, final String nickname) {
        final ChatMessageResponseDTO chatMessageResponseDTO = ChatMessageResponseDTO.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .nickname(nickname)
                .imageURL(chatMessage.getImageURL())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt())
                .build();

        chatMessageResponseDTO.setIsUpdated(chatMessage.getUpdatedAt());

        return chatMessageResponseDTO;
    }

    public static ChatMessageResponseDTO toDTO(final ChatMessage chatMessage) {
        final ChatMessageResponseDTO chatMessageResponseDTO = ChatMessageResponseDTO.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .imageURL(chatMessage.getImageURL())
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