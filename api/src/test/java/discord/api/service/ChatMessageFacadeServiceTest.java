package discord.api.service;

import discord.api.entity.document.ChatMessage;
import discord.api.entity.dtos.chatmessage.ChatMessageResponseDTO;
import discord.api.repository.User.UserRepository;
import discord.api.repository.chatmessage.ChatMessageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageFacadeServiceTest {

    private static final int CHAT_MESSAGE_LIMIT = 300;

    @InjectMocks
    private ChatMessageFacadeService target;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AwsService awsService;

    @Test
    void getChatMessages() {
        final long roomId = 1L;
        final List<Object[]> userList = createUserList();
        final List<ChatMessage> chatMessageList = createChatMessageList(roomId, CHAT_MESSAGE_LIMIT);

        final SliceImpl<ChatMessage> slice = new SliceImpl<>(chatMessageList, PageRequest.ofSize(CHAT_MESSAGE_LIMIT), true);
        
        when(chatMessageRepository.findByRoomIdOrderByIdDesc(any(Long.class), any(PageRequest.class))).thenReturn(slice);
        when(awsService.downloadFile(any(String.class))).thenReturn("testImageCode");
        when(userRepository.getUserListByServerId(any(Long.class))).thenReturn(userList);

        final Slice<ChatMessageResponseDTO> result = target.getChatMessages(roomId);
        final List<ChatMessageResponseDTO> content = result.getContent();

        then(chatMessageRepository).should(times(1)).findByRoomIdOrderByIdDesc(roomId, PageRequest.ofSize(CHAT_MESSAGE_LIMIT));
        then(awsService).should(times(CHAT_MESSAGE_LIMIT)).downloadFile("testFileName");
        then(userRepository).should(times(1)).getUserListByServerId(roomId);

        final List<String> collect = userList.stream().map(user -> (String) user[1]).toList();

        Assertions.assertThatList(content).extracting(ChatMessageResponseDTO::getImageCode).containsOnly("testImageCode");
        Assertions.assertThatList(content).extracting(ChatMessageResponseDTO::getNickname).containsExactly(collect.toArray(new String[0]));
    }

    @Test
    void getMoreChatMessages() {
        final long roomId = 1L;
        final long lastChatId = 500L;
        final List<Object[]> userList = createUserList();
        final List<ChatMessage> chatMessageList = createChatMessageList(roomId, lastChatId);

        final SliceImpl<ChatMessage> slice = new SliceImpl<>(chatMessageList, PageRequest.ofSize(CHAT_MESSAGE_LIMIT), true);

        when(chatMessageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(any(Long.class), any(Long.class), any(PageRequest.class))).thenReturn(slice);
        when(awsService.downloadFile(any(String.class))).thenReturn("testImageCode");
        when(userRepository.getUserListByServerId(any(Long.class))).thenReturn(userList);

        final Slice<ChatMessageResponseDTO> result = target.getMoreChatMessages(roomId, lastChatId);
        final List<ChatMessageResponseDTO> content = result.getContent();

        then(chatMessageRepository).should(times(1)).findByRoomIdAndIdLessThanOrderByIdDesc(roomId, lastChatId, PageRequest.ofSize(CHAT_MESSAGE_LIMIT));
        then(awsService).should(times(CHAT_MESSAGE_LIMIT)).downloadFile("testFileName");
        then(userRepository).should(times(1)).getUserListByServerId(roomId);

        final List<String> collect = userList.stream().map(user -> (String) user[1]).toList();

        Assertions.assertThatList(content).extracting(ChatMessageResponseDTO::getImageCode).containsOnly("testImageCode");
        Assertions.assertThatList(content).extracting(ChatMessageResponseDTO::getNickname).containsExactly(collect.toArray(new String[0]));
    }


    private List<ChatMessage> createChatMessageList(final long roomId,
                                                    final long lastChatId) {
        final LocalDateTime now = LocalDateTime.now();
        final List<ChatMessage> chatMessageList = new ArrayList<>();
        for(int i = 1; i <= CHAT_MESSAGE_LIMIT; i++) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .id(lastChatId - i)
                    .roomId(roomId)
                    .userId(i)
                    .message("test")
                    .fileName("testFileName")
                    .createdAt(now.plusMinutes(300 - i))
                    .updatedAt(now.plusMinutes(300 - i))
                    .build();

            chatMessageList.add(chatMessage);
        }

        return chatMessageList;
    }



    public List<Object[]> createUserList() {
        final List<Object[]> userList = new ArrayList<>();
        for(int i = 1; i <= 300; i++) {
            userList.add(new Object[]{(long) i, "testUser" + i});
        }
        return userList;
    }





}