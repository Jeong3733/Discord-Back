package com.hansori.ws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = WsApplication.class
)
public class StompSupport {

    protected StompSession stompSession;


    private String url;

    private final String ENDPOINT = "/ws-stomp";

    private WebSocketStompClient webSocketStompClient;


    public StompSupport() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(createTransport()));
        this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.url = "ws://localhost:";
    }

    @BeforeEach
    public void connect() throws InterruptedException, ExecutionException, TimeoutException {
        this.stompSession = this.webSocketStompClient
                .connect(url + "8080" + ENDPOINT, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);
    }

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()) {
            this.stompSession.disconnect();
        }
    }


    private List<Transport> createTransport() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }
}
