package com.hansori.ws.stomp;

import com.hansori.ws.db.mysql.user.UserStatus;
import com.hansori.ws.stomp.common.SessionManager;
import com.hansori.ws.stomp.common.utils.JwtUtils;
import com.hansori.ws.stomp.common.utils.StompUtils;
import com.hansori.ws.stomp.service.UserStatusFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final UserStatusFacadeService userStatusFacadeService;
    private final SessionManager sessionManager;


    @EventListener
    public void handWebSocketConnectListener(SessionConnectEvent sessionConnectEvent) {

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
        final long userId = JwtUtils.getUserIdFromToken(StompUtils.getAuthorizationToken(headerAccessor));

        userStatusFacadeService.updateUserStatus(userId, UserStatus.ONLINE);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        final String sessionId = headerAccessor.getSessionId();

        final Long userId = sessionManager.get(sessionId);

        if (userId != null) {
            userStatusFacadeService.updateUserStatus(userId, UserStatus.OFFLINE);
        }

        sessionManager.remove(sessionId);
    }


    @EventListener
    public void handWebSocketSubscribeListener(SubscribeEvent subscribeEvent) {
        userStatusFacadeService.updateUserServerStatus(subscribeEvent.getRoomId(),subscribeEvent.getUserId(), UserStatus.ONLINE);
    }
}
