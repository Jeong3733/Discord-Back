package discord.api.repository.chatmessage;

import discord.api.entity.document.ChatMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    private final int CHAT_MESSAGE_LIMIT = 300;

    @Autowired
    private ChatMessageRepository target;

    @BeforeEach
    void setUp() {

        final LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 500; i++) {
            target.save(ChatMessage.builder()
                    .id(i)
                    .roomId(1)
                    .createdAt(now.plusMinutes(i))
                    .build());
        }

    }

    @AfterEach
    void tearDown() {
        target.deleteAll();
    }

    @Test
    void findByRoomIdLatest() {

        final List<ChatMessage> chatMessages = target.findByRoomIdOrderByIdDesc(1, Pageable.ofSize(CHAT_MESSAGE_LIMIT)).getContent();
        assertEquals(CHAT_MESSAGE_LIMIT, chatMessages.size());
        assertEquals(500, chatMessages.get(0).getId());
        assertEquals(500 - CHAT_MESSAGE_LIMIT + 1, chatMessages.get(299).getId());
        Assertions.assertThatList(chatMessages).isSortedAccordingTo((o1, o2) -> (int) (o2.getId() - o1.getId()));
    }

    @Test
    void findByRoomIdAndIdLessThanOrderByCreatedAtDesc() {

        final long lastChatId = 301;

        final Slice<ChatMessage> firstPage = target.findByRoomIdAndIdLessThanOrderByIdDesc(1, lastChatId, Pageable.ofSize(CHAT_MESSAGE_LIMIT));
        final List<ChatMessage> content = firstPage.getContent();
        assertEquals(CHAT_MESSAGE_LIMIT, firstPage.getContent().size());

        assertEquals(lastChatId - 1, content.get(0).getId());
        assertEquals(lastChatId - CHAT_MESSAGE_LIMIT, content.get(299).getId());
        Assertions.assertThatList(content).isSortedAccordingTo((o1, o2) -> (int) (o2.getId() - o1.getId()));
    }




}