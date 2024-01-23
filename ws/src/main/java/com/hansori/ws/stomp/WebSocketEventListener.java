package com.hansori.ws.stomp;

import com.hansori.ws.db.mysql.user.UserStatus;
import com.hansori.ws.stomp.common.utils.JwtUtils;
import com.hansori.ws.stomp.common.utils.StompUtils;
import com.hansori.ws.stomp.service.UserStatusFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserStatusFacadeService userStatusFacadeService;

    @EventListener
    public void handWebSocketConnectListener(SessionConnectEvent sessionConnectEvent) {

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
        final long roomId = Long.parseLong(StompUtils.getRoomIdFromDestination(headerAccessor.getDestination()));
        final long userId = JwtUtils.getUserIdFromToken(StompUtils.getAuthorizationToken(headerAccessor));

        userStatusFacadeService.updateStatus(roomId, userId, UserStatus.ONLINE);

    }

    @EventListener
    public void handWebSocketDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        final long roomId = Long.parseLong(StompUtils.getRoomIdFromDestination(headerAccessor.getDestination()));
        final long userId = JwtUtils.getUserIdFromToken(StompUtils.getAuthorizationToken(headerAccessor));

        userStatusFacadeService.updateStatus(roomId, userId, UserStatus.OFFLINE);
    }
}
