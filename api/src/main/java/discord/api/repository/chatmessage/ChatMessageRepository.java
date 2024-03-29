package discord.api.repository.chatmessage;

import discord.api.entity.document.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long> {

    Slice<ChatMessage> findByRoomIdOrderByIdDesc(long roomId, Pageable pageable);

    Slice<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(long roomId, long chatId, Pageable pageable);


}

