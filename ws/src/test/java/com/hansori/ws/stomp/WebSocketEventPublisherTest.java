package com.hansori.ws.stomp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class WebSocketEventPublisherTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private WebSocketEventListener webSocketEventListener;

    private Message message;
    private MessageHeaders messageHeaders;

    @BeforeEach
    void setUp() {
        message = mock(Message.class);
        messageHeaders = mock(MessageHeaders.class);

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get("simpMessageType")).thenReturn("CONNECT");
        when(messageHeaders.get("simpSessionId")).thenReturn("0");
        when(messageHeaders.get("simpSessionAttributes")).thenReturn(null);
        when(messageHeaders.get("simpHeartbeat")).thenReturn(new long[]{10000, 10000});
        when(messageHeaders.get("stompCommand")).thenReturn("CONNECT");
        when(messageHeaders.get("nativeHeaders")).thenReturn(null);
        when(messageHeaders.get("simpDestination")).thenReturn("/topic/1");
        when(messageHeaders.get("simpUser")).thenReturn(null);
        when(messageHeaders.get("simpSubscriptionId")).thenReturn("0");
        when(messageHeaders.get("simpConnectMessage")).thenReturn(null);
        when(messageHeaders.get("simpDisconnectMessage")).thenReturn(null);
    }

    @Test
    void handWebSocketConnectListener() {

        final SessionConnectEvent sessionConnectEvent = new SessionConnectEvent("test", message, null);

        applicationEventPublisher.publishEvent(sessionConnectEvent);

        Mockito.verify(webSocketEventListener, Mockito.times(1)).handWebSocketConnectListener(sessionConnectEvent);
    }

    @Test
    void handWebSocketDisconnectListener() {

        final SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);

        when(disconnectEvent.getMessage()).thenReturn(message);
        when(disconnectEvent.getSessionId()).thenReturn("0");


        applicationEventPublisher.publishEvent(disconnectEvent);

        Mockito.verify(webSocketEventListener, Mockito.times(1)).handleWebSocketDisconnectListener(disconnectEvent);
    }


}