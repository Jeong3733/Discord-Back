package com.hansori.ws.db.mongo.auto_sequence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "auto_increment_sequence")
public class AutoIncrementSequence {

    @Id
    private String id;
    private long seq;
}
