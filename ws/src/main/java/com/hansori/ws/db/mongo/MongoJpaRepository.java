package com.hansori.ws.db.mongo;

import com.hansori.ws.db.mongo.document.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoJpaRepository extends MongoRepository<ChatMessage, Long> {



    Slice<ChatMessage> findByRoomIdOrderByCreatedAtDesc(long roomId, Pageable pageable);

    Slice<ChatMessage> findByRoomIdAndIdLessThanOrderByCreatedAtDesc(long roomId, long chatId, Pageable pageable);
}
