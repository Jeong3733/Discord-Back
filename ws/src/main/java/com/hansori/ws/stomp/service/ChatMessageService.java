package com.hansori.ws.stomp.service;

import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.db.mongo.MongoJpaRepository;
import com.hansori.ws.db.mongo.auto_sequence.SequenceGenerator;
import com.hansori.ws.db.mysql.user.UserRepository;
import com.hansori.ws.stomp.dto.request.ChatMessageRequestDTO;
import com.hansori.ws.stomp.dto.response.ChatMessageResponseDTO;
import com.hansori.ws.stomp.dto.response.error.CustomException;
import com.hansori.ws.stomp.dto.response.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {


    private final SequenceGenerator sequenceGenerator;
    private final MongoJpaRepository mongoJpaRepository;
    private final FileService fileService;
    private final UserRepository userRepository;


    public ChatMessage save(final ChatMessageRequestDTO chatMessageRequestDTO, final long roomId, final long userId) {


        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        final ChatMessage chatMessage = chatMessageRequestDTO.getImageCode() == null ?
                chatMessageRequestDTO.toEntity(roomId, userId) :
                chatMessageRequestDTO.toEntity(roomId, userId, fileService.uploadFile(chatMessageRequestDTO.getImageCode()));

        chatMessage.setId(sequenceGenerator.generateSequence(ChatMessage.SEQUENCE_NAME));
        mongoJpaRepository.save(chatMessage);

        return chatMessage;
    }


    public Slice<ChatMessageResponseDTO> findAll(final long roomId, final long chatId) {
        final Slice<ChatMessage> result = mongoJpaRepository.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(roomId, chatId, PageRequest.ofSize(300));

        final Map<Long, String> nicknameMap = userRepository.getUserListByServerId(roomId)
                .stream().collect(Collectors.toMap(
                        user -> (long) user[0],
                        user -> (String) user[1]
                ));

        return result.map(chatMessage -> {
            String imageCode = null;
            if(chatMessage.getFileName() != null){
                imageCode = fileService.downloadFile(chatMessage.getFileName());
            }

            ChatMessageResponseDTO chatMessageResponseDTO = ChatMessageResponseDTO.toDTO(chatMessage, imageCode);
            chatMessageResponseDTO.setNickname(nicknameMap.get(chatMessage.getUserId()));
            return chatMessageResponseDTO;
        });

    }

}
