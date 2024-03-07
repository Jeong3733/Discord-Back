package discord.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import discord.api.entity.dtos.chatmessage.ChatMessageResponseDTO;
import discord.api.service.ChatMessageFacadeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @InjectMocks
    private ChatController target;

    @Mock
    private ChatMessageFacadeService chatService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .build();
    }


    @Test
    void getChatMessages() throws Exception {
        final long roomId = 1;
        final String url = "/server/" + roomId;

        final List<ChatMessageResponseDTO> content = createContent(300);
        final Slice<ChatMessageResponseDTO> response = new SliceImpl<>(content, Pageable.ofSize(300), false);

        when(chatService.getChatMessages(any(Long.class))).thenReturn(response);

        final MockHttpServletResponse mockResponse = mockMvc.perform(
                        get(url)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        then(chatService).should(times(1)).getChatMessages(argumentCaptor.capture());

        assertEquals(roomId, argumentCaptor.getValue());

        Assertions.assertEquals(
                objectMapper.writeValueAsString(response),
                mockResponse.getContentAsString(StandardCharsets.UTF_8));

    }

    @Test
    void getMoreChatMessages() throws Exception {
        final long roomId = 1;
        final long lastChatId = 100;
        final String url = "/server/" + roomId + "/chat/" + lastChatId;

        final List<ChatMessageResponseDTO> content = createContent(lastChatId);
        final Slice<ChatMessageResponseDTO> response = new SliceImpl<>(content, Pageable.ofSize(300), false);

        when(chatService.getMoreChatMessages(any(Long.class), any(Long.class))).thenReturn(response);

        final MockHttpServletResponse mockResponse = mockMvc.perform(
                        get(url)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final ArgumentCaptor<Long> roomIdCaptor = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<Long> lastChatIdCaptor = ArgumentCaptor.forClass(Long.class);
        then(chatService).should(times(1)).getMoreChatMessages(roomIdCaptor.capture(), lastChatIdCaptor.capture());

        assertEquals(roomId, roomIdCaptor.getValue());
        assertEquals(lastChatId, lastChatIdCaptor.getValue());

        Assertions.assertEquals(
                objectMapper.writeValueAsString(response),
                mockResponse.getContentAsString(StandardCharsets.UTF_8));
    }

    public List<ChatMessageResponseDTO> createContent(final long lastChatId) {

        final List<ChatMessageResponseDTO> content = new ArrayList<>();
        for(int i = 1; i<= 300; i++) {
            content.add(ChatMessageResponseDTO.builder()
                    .id(lastChatId - i)
                    .userId(i)
                    .nickname("testNickname" + i)
                    .imageCode("testImageCode" + i)
                    .message("testMessage" + i)
                    .build());
        }

        return content;
    }

}