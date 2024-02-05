package com.hansori.ws.stomp;

import com.hansori.ws.db.mysql.user.UserStatus;
import com.hansori.ws.stomp.common.SessionManager;
import com.hansori.ws.stomp.common.utils.JwtUtils;
import com.hansori.ws.stomp.common.utils.StompUtils;
import com.hansori.ws.stomp.service.UserStatusFacadeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketEventListenerTest {

    @InjectMocks
    private WebSocketEventListener target;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserStatusFacadeService userStatusFacadeService;

    final MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = mockStatic(StompHeaderAccessor.class);
    final MockedStatic<JwtUtils> jwtUtils = mockStatic(JwtUtils.class);
    final MockedStatic<StompUtils> stompUtils = mockStatic(StompUtils.class);

    @BeforeEach
    public void setUp() {

        jwtUtils.when(() -> JwtUtils.getUserIdFromToken(any(String.class))).thenReturn(1L);
        stompUtils.when(() -> StompUtils.getAuthorizationToken(any(StompHeaderAccessor.class))).thenReturn("token");

    }

    @AfterEach
    public void tearDown() {
        stompHeaderAccessorMockedStatic.close();
        jwtUtils.close();
        stompUtils.close();
    }


    @Test
    void handWebSocketConnectListener() {

        final SessionConnectEvent sessionConnectEvent = mock(SessionConnectEvent.class);
        final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);
        final ArgumentCaptor<UserStatus> argumentCaptor = ArgumentCaptor.forClass(UserStatus.class);

        stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(sessionConnectEvent.getMessage())).thenReturn(stompHeaderAccessor);
        target.handWebSocketConnectListener(sessionConnectEvent);



        Mockito.verify(userStatusFacadeService, Mockito.times(1)).updateUserStatus(any(Long.class), argumentCaptor.capture());
        assert(argumentCaptor.getValue().equals(UserStatus.ONLINE));
    }

    @Test
    void handWebSocketDisconnectListener() {


        final SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        final ArgumentCaptor<UserStatus> argumentCaptor = ArgumentCaptor.forClass(UserStatus.class);
        final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

        stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(disconnectEvent.getMessage())).thenReturn(stompHeaderAccessor);
        when(stompHeaderAccessor.getSessionId()).thenReturn("0");
        when(sessionManager.get("0")).thenReturn(1L);

        target.handleWebSocketDisconnectListener(disconnectEvent);

        Mockito.verify(userStatusFacadeService, Mockito.times(1)).updateUserStatus(any(Long.class), argumentCaptor.capture());
        assert(argumentCaptor.getValue().equals(UserStatus.OFFLINE));
    }

}
