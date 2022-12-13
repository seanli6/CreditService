package com.itservice.creditservice.service;

import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/*
 * This class is used for regular KafkaConsumer which doesn't have the functions to expose the received message back to client as Flux
 * 
 * In CreditController, reactor.kafka.receiver.KafkaReceiver is created for listener 
 * 
 */
@Service
@Slf4j
public class ProcessingMsgConsumer {
	
    @Value("${spring.kafka.topic-name}")
    private String topicName;
    
    @Value("${spring.kafka.group-name}")
    private String groupName;

//	@KafkaListener(topics = "my-topic-1", groupId = "sean-group")
//	public String listenToCsvProcessTopic(ConsumerRecord<String, String> cr,
//            @Payload String payload ){
//		log.info("Logger 1 [JSON] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
//                typeIdHeader(cr.headers()), payload, cr.toString());
//		return payload;
//	}
//	
	
    private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
}

