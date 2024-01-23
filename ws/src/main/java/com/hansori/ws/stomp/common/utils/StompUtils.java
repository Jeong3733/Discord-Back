package com.hansori.ws.stomp.common.utils;

import com.hansori.ws.stomp.common.constant.JwtConstant;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class StompUtils {

    public static String getRoomIdFromDestination(final String destination) {
        return destination.substring(destination.lastIndexOf("/") + 1);
    }

    public static String getAuthorizationToken(final StompHeaderAccessor stompHeaderAccessor) {
        return stompHeaderAccessor.getFirstNativeHeader(JwtConstant.TOKEN_HEADER);
    }
}
