package com.itservice.creditservice.service;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class ProcessingMsgProducer {
	
    @Value("${spring.kafka.topic-name}")
    private String topicName;
    
    @Value("${spring.kafka.group-name}")
    private String groupName;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessageToTopic(String message, String sessionId) {
		log.info("calling sendMessageToTopic for message: " + message);
//		kafkaTemplate.send("my-topic-2",sessionId, message);
//		IntStream.range(0, 5).forEach(c-> kafkaTemplate.send("csv-process-topic","SSSSSSSSSSSSSSSSSKEY" + c,message));
		
	       Message<String> messageObj = MessageBuilder
	                .withPayload(message)
	                .setHeader(KafkaHeaders.TOPIC, topicName)
	                .setHeader(KafkaHeaders.MESSAGE_KEY, sessionId)
//	                .setHeader(KafkaHeaders.PARTITION_ID, 1)
	                .setHeader("X-Custom-Header", "Sending Custom Header with Spring Kafka")
	                .build();

	        kafkaTemplate.send(messageObj);
	}

}