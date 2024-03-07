package com.hansori.ws.stomp.common.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import com.hansori.ws.stomp.dto.response.error.ErrorResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Objects;


@Component
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    public StompErrorHandler(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {


        log.error("handleClientMessageProcessingError: {}", ex.getMessage());

        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        final String errorMessage = createErrorMessage(ErrorCode.USER_NOT_AUTHORIZED);

        accessor.setMessage(errorMessage);
        accessor.setLeaveMutable(true);

        setReceiptIdForClient(clientMessage, accessor);

        return MessageBuilder.createMessage(
                errorMessage.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );
    }

    private String createErrorMessage(final ErrorCode errorCode) throws JsonProcessingException {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.name())
                .code(errorCode.getCode())
                .build();
        return objectMapper.writeValueAsString(errorResponse);
    }

    private void setReceiptIdForClient(final Message<byte[]> clientMessage,
                                       final StompHeaderAccessor accessor) {

        if (Objects.isNull(clientMessage)) {
            return;
        }

        final StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(
                clientMessage, StompHeaderAccessor.class);

        final String receiptId =
                Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();

        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }
}
