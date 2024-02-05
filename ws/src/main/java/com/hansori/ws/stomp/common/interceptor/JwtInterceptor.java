package com.hansori.ws.stomp.common.interceptor;

import com.hansori.ws.stomp.SubscribeEvent;
import com.hansori.ws.stomp.common.SessionManager;
import com.hansori.ws.stomp.common.utils.JwtUtils;
import com.hansori.ws.stomp.common.constant.JwtConstant;
import com.hansori.ws.stomp.common.utils.StompUtils;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtInterceptor implements ChannelInterceptor {

    private final SessionManager sessionManager;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor wrap = StompHeaderAccessor.wrap(message);
        final String token = wrap.getFirstNativeHeader(JwtConstant.TOKEN_HEADER);
        final String sessionId = wrap.getSessionId();

        if(wrap.getCommand() != StompCommand.SUBSCRIBE && wrap.getCommand() != StompCommand.DISCONNECT) {
            if(token == null || !JwtUtils.validateToken(token)) {
                throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
            }
        }


        switch (wrap.getCommand()) {
            case CONNECT -> {
                final long userId = JwtUtils.getUserIdFromToken(token);
                sessionManager.put(sessionId, userId);
            }
            case SUBSCRIBE -> {
                final long userId = sessionManager.get(sessionId);
                final String destination = StompUtils.getRoomIdFromDestination(wrap.getDestination());
                if(!destination.equals("errors")) {
                    applicationEventPublisher.publishEvent(new SubscribeEvent(userId, Long.parseLong(destination)));
                }
            }
        }
        return message;
    }
}
