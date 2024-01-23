package com.hansori.ws.stomp.common.interceptor;

import com.hansori.ws.stomp.common.utils.JwtUtils;
import com.hansori.ws.stomp.common.constant.JwtConstant;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor wrap = StompHeaderAccessor.wrap(message);

        final String token = wrap.getFirstNativeHeader(JwtConstant.TOKEN_HEADER);

        if(StompCommand.DISCONNECT.equals(wrap.getCommand()) || StompCommand.SUBSCRIBE.equals(wrap.getCommand())) {
            return message;
        }

        if(token == null || !JwtUtils.validateToken(token)) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        return message;
    }
}
