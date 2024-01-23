package com.hansori.ws.stomp.common.config;

import com.hansori.ws.stomp.common.interceptor.JwtInterceptor;
import com.hansori.ws.stomp.common.handler.CustomHandshakeHandler;
import com.hansori.ws.stomp.common.handler.StompErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompErrorHandler stompErrorHandler;
    private final JwtInterceptor jwtInterceptor;
    private final CustomHandshakeHandler customHandshakeHandler;

    /**
     *
     * org.apache.tomcat.websocket.WsFrameBase 에 ping 핸들러 있어서 구현 X
     * https://github.com/apache/tomcat/blob/a903a28dd14055b6537a33904baef875b68678d8/java/org/apache/tomcat/websocket/WsFrameBase.java#L348-L351
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지 브로커가 /sub 로 시작하는 대상에게 메시지를 전달하도록 설정
        registry.enableSimpleBroker("/sub", "/queue");

        // /pub 으로 시작하는 메시지가 message-handling methods 로 라우팅 되도록 설정
        registry.setApplicationDestinationPrefixes("/pub");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

            // /ws-stomp 로 시작하는 WebSocket 연결을 허용
        registry.addEndpoint("/ws-stomp")
                .addInterceptors()
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOrigins("*");

        registry.setErrorHandler(stompErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtInterceptor);

    }

    @Bean
    public ServletServerContainerFactoryBean serverContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 10MB
        container.setMaxTextMessageBufferSize(1024 * 1024 * 10);
        container.setMaxBinaryMessageBufferSize(1024 * 1024 * 10);

        // 5 minutes
        // https://www.appsloveworld.com/springboot/100/2/disconnect-client-session-from-spring-websocket-stomp-server
        container.setMaxSessionIdleTimeout(5*60*1000L);


        return container;
    }
}
