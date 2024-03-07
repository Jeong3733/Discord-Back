package com.hansori.ws.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansori.ws.kafka.KafkaConstant;
import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.stomp.dto.request.ChatMessageRequestDTO;
import com.hansori.ws.stomp.dto.response.ChatMessageResponseDTO;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import com.hansori.ws.stomp.dto.response.error.ErrorResponse;
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
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @MessageMapping("/chat/{roomId}")
    public void message(@DestinationVariable final long roomId, final ChatMessageRequestDTO message, final StompHeaderAccessor stompHeaderAccessor) {

        final long userId = getUserIdFromToken(getAuthorizationToken(stompHeaderAccessor));
        kafkaTemplate.send(KafkaConstant.TOPIC_CHAT, chatMessageService.save(message, roomId, userId));
    }


    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(final CustomException customException) throws JsonProcessingException {
        final ErrorCode errorCode = customException.getErrorCode();

        log.error("handleException: {}", errorCode);

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.name())
                .code(errorCode.getCode())
                .build();


        return objectMapper.writeValueAsString(errorResponse);    }



}
