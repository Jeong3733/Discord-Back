package com.hansori.ws.stomp.common.interceptor;

import com.hansori.ws.JwtTokenProvider;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.mockito.Mockito.*;

class JwtInterceptorTest {


    private JwtInterceptor target = new JwtInterceptor();



    @Test
    void preHandle() {
        final String token = JwtTokenProvider.generateToken(1L);


        try (MockedStatic<StompHeaderAccessor> stompHeaderAccessorMockedStatic = Mockito.mockStatic(StompHeaderAccessor.class)) {

            final Message<?> message = mock(Message.class);
            final MessageChannel messageChannel = mock(MessageChannel.class);
            final StompHeaderAccessor stompHeaderAccessor = mock(StompHeaderAccessor.class);

            stompHeaderAccessorMockedStatic.when(() -> StompHeaderAccessor.wrap(message)).thenReturn(stompHeaderAccessor);
            when(stompHeaderAccessor.getFirstNativeHeader(any(String.class))).thenReturn(token);

            Assertions.assertDoesNotThrow(() -> target.preSend(message, messageChannel));

            stompHeaderAccessorMockedStatic.verify(() -> StompHeaderAccessor.wrap(message), times(1));
            verify(stompHeaderAccessor, times(1)).getFirstNativeHeader(any(String.class));
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


            CustomException customException = Assertions.assertThrows(CustomException.class, () -> target.preSend(message, messageChannel));

            Assertions.assertEquals(ErrorCode.USER_NOT_AUTHORIZED, customException.getErrorCode());
            stompHeaderAccessorMockedStatic.verify(() -> StompHeaderAccessor.wrap(message), times(1));
            verify(stompHeaderAccessor, times(1)).getFirstNativeHeader(any(String.class));

        }
    }

}