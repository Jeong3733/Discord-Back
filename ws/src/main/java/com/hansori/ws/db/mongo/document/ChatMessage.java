package com.hansori.ws.db.mongo.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Document(collection = "message")
public class ChatMessage {


    @Transient
    public static final String SEQUENCE_NAME = "chat_message_sequence";

    @Id
    private long id;
    private long userId;
    private long roomId;
    private String fileName;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    public void setId(long id) {
        this.id = id;
    }

}
