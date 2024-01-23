package com.hansori.ws.stomp;

import com.hansori.ws.kafka.KafkaConstant;
import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.stomp.dto.request.ChatMessageRequestDTO;
import com.hansori.ws.stomp.dto.response.ChatMessageResponseDTO;
import com.hansori.ws.stomp.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import static com.hansori.ws.stomp.common.utils.JwtUtils.*;
import static com.hansori.ws.stomp.common.utils.StompUtils.*;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<Slice<ChatMessageResponseDTO>> getChatMessage(@PathVariable final int roomId, final long chatId) {
        return ResponseEntity.ok(chatMessageService.findAll(roomId, chatId));
    }

    @MessageMapping("/chat/{roomId}")
    public void message(@DestinationVariable long roomId, final ChatMessageRequestDTO message, final StompHeaderAccessor stompHeaderAccessor) {

        log.info("message");
        long userId = getUserIdFromToken(getAuthorizationToken(stompHeaderAccessor));
        kafkaTemplate.send(KafkaConstant.TOPIC_CHAT, chatMessageService.save(message, roomId, userId));
    }


    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception exception) {
        exception.printStackTrace();

        log.error("handleException");
        return "Unknown error";
    }



}
