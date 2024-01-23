package com.hansori.ws;

import com.hansori.ws.stomp.dto.request.ChatMessageRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketTest extends StompSupport{

    @Test
    public void findUsers() throws ExecutionException, InterruptedException, TimeoutException, ExecutionException, TimeoutException {
        /* GIVEN */
        MessageFrameHandler<ChatMessageRequestDTO[]> handler = new MessageFrameHandler<>(ChatMessageRequestDTO[].class);
        this.stompSession.subscribe("/sub/chat/1", handler);


        ChatMessageRequestDTO chatMessageRequestDTO = ChatMessageRequestDTO.builder()
                .message("hello")
                .build();
        /* WHEN */
        this.stompSession.send("/pub/chat/1", chatMessageRequestDTO);

        /* THEN */
        ChatMessageRequestDTO[] chatMessageRequestDTOS = handler.getCompletableFuture().get(3, TimeUnit.SECONDS);

        for (ChatMessageRequestDTO messageRequestDTO : chatMessageRequestDTOS) {
            System.out.println(messageRequestDTO);
        }
    }
}
