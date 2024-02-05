package discord.api.repository.chatmessage;

import discord.api.entity.document.ChatMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockBean(JpaMetamodelMappingContext.class)
@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository target;

    @Test
    void findByRoomIdLatest() {

        final LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 300; i++) {
            target.save(ChatMessage.builder()
                    .id(i)
                    .roomId(1)
                    .createdAt(now.plusMinutes(i))
                    .build());
        }

        final List<ChatMessage> chatMessages = target.findByRoomIdOrderByCreatedAtDesc(1, Pageable.ofSize(100)).getContent();
        assertEquals(100, chatMessages.size());
        assertEquals(300, chatMessages.get(0).getId());
        assertEquals(201, chatMessages.get(99).getId());
        Assertions.assertThatList(chatMessages).isSortedAccordingTo((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
    }

    @Test
    void findByRoomIdAndIdLessThanOrderByCreatedAtDesc() {

        final LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 300; i++) {
            target.save(ChatMessage.builder()
                    .id(i)
                    .roomId(1)
                    .createdAt(now.plusMinutes(i))
                    .build());
        }


        final Slice<ChatMessage> firstPage = target.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(1, 301, Pageable.ofSize(100));
        final List<ChatMessage> content = firstPage.getContent();
        assertEquals(100, firstPage.getContent().size());

        assertEquals(300, content.get(0).getId());
        assertEquals(201, content.get(99).getId());
        Assertions.assertThatList(content).isSortedAccordingTo((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
    }

    @Test
    void findByRoomIdAndIdLessThanOrderByCreatedAtDesc2() {
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 300; i++) {
            target.save(ChatMessage.builder()
                    .id(i)
                    .roomId(1)
                    .createdAt(now.plusMinutes(i))
                    .build());
        }


        final Slice<ChatMessage> firstPage = target.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(1, 201, Pageable.ofSize(100));
        final List<ChatMessage> content = firstPage.getContent();
        assertEquals(100, firstPage.getContent().size());

        assertEquals(200, content.get(0).getId());
        assertEquals(101, content.get(99).getId());
        Assertions.assertThatList(content).isSortedAccordingTo((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

    }



}