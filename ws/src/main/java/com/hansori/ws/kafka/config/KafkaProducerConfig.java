package com.hansori.ws.kafka.config;

import com.hansori.ws.db.mongo.document.ChatMessage;
import com.hansori.ws.kafka.KafkaConstant;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


    //todo: kafka producer factory 뒤에 타입 수정 해야함
    @Bean
    public ProducerFactory<String, ChatMessage> producerChatMessageFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.BOOTSTRAP_SERVERS_CONFIG);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    @Qualifier("kafkaChatMessageTemplate")
    public KafkaTemplate<String, ChatMessage> kafkaMessageTemplate() {
        return new KafkaTemplate<>(producerChatMessageFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerAlarmMessageFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    @Qualifier("kafkaAlarmMessageTemplate")
    public KafkaTemplate<String, String> kafkaAlarmMessageTemplate() {
        return new KafkaTemplate<>(producerAlarmMessageFactory());
    }
}
