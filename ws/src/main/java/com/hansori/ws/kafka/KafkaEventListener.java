package com.hansori.ws.kafka;

import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.db.mysql.user.UserRepository;
import com.hansori.ws.stomp.dto.response.ChatMessageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    @KafkaListener(topics = KafkaConstant.TOPIC_CHAT, groupId = KafkaConstant.GROUP_ID)
    public void kafkaListener(final ChatMessage chatMessage) {

        final String nickname = userRepository.getNicknameByUserId(chatMessage.getUserId());
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatMessage.getRoomId(),
                ChatMessageResponseDTO.toDTO(chatMessage, nickname));
    }

}
