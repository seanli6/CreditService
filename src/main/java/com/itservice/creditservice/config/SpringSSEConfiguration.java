package com.itservice.creditservice.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
 
@Configuration
class SpringSSEConfiguration {
	
    @Value("${spring.kafka.topic-name}")
    private String topicName;
    
    @Value("${spring.kafka.group-name}")
    private String groupName;
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    
	@Bean
	KafkaReceiver kafkaReceiver(){
//		String bootstrapAddress = "0.0.0.0:9092";
 
		Map<String, Object> configProps = new HashMap<>();
		configProps.put( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put( ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		configProps.put( ConsumerConfig.CLIENT_ID_CONFIG, "sample-client");
		configProps.put( ConsumerConfig.GROUP_ID_CONFIG, groupName);
		configProps.put( ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
 
		return new DefaultKafkaReceiver(
			ConsumerFactory.INSTANCE,
			ReceiverOptions.create(configProps).subscription(List.of(topicName))
		);
	}
}