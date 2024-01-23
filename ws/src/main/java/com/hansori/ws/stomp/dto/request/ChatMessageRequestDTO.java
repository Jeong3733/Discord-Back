package com.hansori.ws.stomp.dto.request;

import com.hansori.ws.db.mongo.document.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class ChatMessageRequestDTO {

    private String message;
    private String imageCode;


    public ChatMessage toEntity(long roomId, long userId) {
        return ChatMessage.builder()
                .message(message)
                .userId(userId)
                .roomId(roomId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    public ChatMessage toEntity(long roomId, long userId, String imageCode) {
        return ChatMessage.builder()
                .message(message)
                .userId(userId)
                .roomId(roomId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

}
