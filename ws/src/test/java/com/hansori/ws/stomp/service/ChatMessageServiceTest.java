package com.hansori.ws.stomp.service;

import com.hansori.ws.db.mongo.MongoJpaRepository;
import com.hansori.ws.db.mongo.auto_sequence.SequenceGenerator;
import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.stomp.dto.request.ChatMessageRequestDTO;
import com.hansori.ws.stomp.dto.response.ChatMessageResponseDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService target;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private MongoJpaRepository mongoJpaRepository;

    @Test
    void save() {

        final ChatMessageRequestDTO chatMessageRequestDTO = ChatMessageRequestDTO.builder()
                .message("test")
                .imageCode("testImageCode")
                .build();

        final long roomId = 1L;
        final long userId = 1L;

        BDDMockito.when(sequenceGenerator.generateSequence("chat_message_sequence")).thenReturn(1L);
        BDDMockito.when(mongoJpaRepository.save(any())).thenReturn(null);

        final ChatMessage result = target.save(chatMessageRequestDTO, roomId, userId);

        BDDMockito.then(sequenceGenerator).should(BDDMockito.times(1)).generateSequence(ChatMessage.SEQUENCE_NAME);
        BDDMockito.then(mongoJpaRepository).should(BDDMockito.times(1)).save(any());

        assertEquals(1L, result.getId());
        assertEquals("test", result.getMessage());
        assertEquals(roomId, result.getRoomId());
        assertEquals(userId, result.getUserId());
    }

}