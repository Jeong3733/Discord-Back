package com.hansori.ws.stomp.common.interceptor;

import com.hansori.ws.JwtTokenProvider;
import com.hansori.ws.stomp.SubscribeEvent;
import com.hansori.ws.stomp.common.SessionManager;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.mockito.Mockito.*;
import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

@ExtendWith(MockitoExtension.class)
class JwtInterceptorTest {


    @InjectMocks
    private JwtInterceptor target;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;


    @Test
    void preHandleConnect() {
        final String token = JwtTokenProvider.generateToken(1L);


        try (MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = Mockito.mockStatic(StompHeaderAccessor.class)) {

            final Message<?> message = mock(Message.class);
            final MessageChannel messageChannel = mock(MessageChannel.class);
            final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

            stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(message)).thenReturn(stompHeaderAccessor);
            when(stompHeaderAccessor.getFirstNativeHeader(any(String.class))).thenReturn(token);
            when(stompHeaderAccessor.getCommand()).thenReturn(CONNECT);


            Assertions.assertDoesNotThrow(() -> target.preSend(message, messageChannel));

            stompHeaderAccessorMockedStatic.verify(() -> StompHeaderAccessor.wrap(message), times(1));
            verify(stompHeaderAccessor, times(1)).getFirstNativeHeader(any(String.class));
        }
    }

    @Test
    void preHandleSubscribe() {


        try (MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = Mockito.mockStatic(StompHeaderAccessor.class)) {

            final Message<?> message = mock(Message.class);
            final MessageChannel messageChannel = mock(MessageChannel.class);
            final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

            stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(message)).thenReturn(stompHeaderAccessor);
            when(stompHeaderAccessor.getFirstNativeHeader(any(String.class))).thenReturn(null);
            when(stompHeaderAccessor.getCommand()).thenReturn(SUBSCRIBE);
            when(stompHeaderAccessor.getDestination()).thenReturn("/chat/1");


            Assertions.assertDoesNotThrow(() -> target.preSend(message, messageChannel));

            verify(applicationEventPublisher, times(1)).publishEvent(any(SubscribeEvent.class));

        }

    }


    @Test
    void preHandleThrowEx() {
        final String wrongToken = "wrongToken";


        try (MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = Mockito.mockStatic(StompHeaderAccessor.class)) {

            final Message<?> message = mock(Message.class);
            final MessageChannel messageChannel = mock(MessageChannel.class);
            final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

            stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(message)).thenReturn(stompHeaderAccessor);
            when(stompHeaderAccessor.getFirstNativeHeader(any(String.class))).thenReturn(wrongToken);
            when(stompHeaderAccessor.getCommand()).thenReturn(CONNECT);


            CustomException customException = Assertions.assertThrows(CustomException.class, () -> target.preSend(message, messageChannel));

            Assertions.assertEquals(ErrorCode.USER_NOT_AUTHORIZED, customException.getErrorCode());
            stompHeaderAccessorMockedStatic.verify(() -> StompHeaderAccessor.wrap(message), times(1));
            verify(stompHeaderAccessor, times(1)).getFirstNativeHeader(any(String.class));
            verify(applicationEventPublisher, times(0)).publishEvent(any());

        }
    }

    @Test
    void preHandleSubscribeEX() {


        try (MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = Mockito.mockStatic(StompHeaderAccessor.class)) {

            final Message<?> message = mock(Message.class);
            final MessageChannel messageChannel = mock(MessageChannel.class);
            final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

            stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(message)).thenReturn(stompHeaderAccessor);
            when(stompHeaderAccessor.getFirstNativeHeader(any(String.class))).thenReturn(null);
            when(stompHeaderAccessor.getCommand()).thenReturn(SUBSCRIBE);
            when(stompHeaderAccessor.getDestination()).thenReturn("/errors");


            Assertions.assertDoesNotThrow(() -> target.preSend(message, messageChannel));

            verify(applicationEventPublisher, times(0)).publishEvent(any(SubscribeEvent.class));

        }

    }

}