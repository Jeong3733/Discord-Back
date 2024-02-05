package com.hansori.ws.stomp.common.handler;

import com.hansori.ws.stomp.dto.response.error.ErrorCode;
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

    public StompErrorHandler() {
        super();
    }


    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {


        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(ErrorCode.USER_NOT_AUTHORIZED.getMessage());
        accessor.setLeaveMutable(true);

        setReceiptIdForClient(clientMessage, accessor);

        return MessageBuilder.createMessage(
                ErrorCode.USER_NOT_AUTHORIZED.toString().getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );
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
