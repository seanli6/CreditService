package com.itservice.creditservice.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.service.CreditService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@RestController
@RequestMapping("/credits")
@Slf4j
public class CreditController {
	
	
	@Autowired
	CreditService creditParseService;
	
	//*************************** Use the Kafka (producer/consumer) Solution ***************************
	
	@Autowired
	KafkaReceiver<String,String> kafkaReceiver;
	
	/*
	 * User upload csv file, creditParseService will prepare and process the data in one call.
	 * 
	 * Processing message will send to Kafka
	 * 
	 */
	@PostMapping("/upload-kfk")
	public Mono<String> loadAndParseCsv_Kafka(HttpSession session, @RequestBody MultipartFile file) {
		log.info("calling loadAndParseCsv_Kafka of CreditController");
		return creditParseService.prepareAndProcessCsvData(file, session.getId());
	}

 
	/*
	 * Use Reactor kafka as the message listener and return message as Flux
	 * 
	 * Client(browser) use URL /credits/processing-kfk to listen the Flux message
	 * 
	 */

	
	@GetMapping(value = "/processing-kfk", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<String> getKafkaMessage(){
		log.info("calling getKafkaMessage of CreditController");
		Flux<ReceiverRecord<String,String>> kafkaFlux = kafkaReceiver.receive();
		return kafkaFlux.log().doOnNext( r -> {
			log.info("Key: "+ r.key() + " / Message: " + r.value());
			r.receiverOffset().acknowledge();
		} )
		.map(ReceiverRecord::value);
	}
	

	//******************************** Use the WebFlux Solution *********************************

	
	/*
	 * This operation only deal with csv file uploading, file formatting and data validation
	 * 
	 * The validated data will be cached in creditParseService and waiting next operation to process
	 *   
	 */
	@PostMapping("/upload")
	public Mono<String> loadAndParseCsv(@RequestBody MultipartFile file) {
		log.info("calling loadAndParseCsv of CreditController");
		return creditParseService.prepareCsvData(file);
	}
	
	/*
	 * Processing and save all data to database in batches. Each batch saving successful message will
	 * be return to caller as Flux
	 * 
	 * Client(browser) use URL /credits/processing to listen the Flux message
	 *  
	 */
	@GetMapping(value = "/processing", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<String> processing(){
		log.info("calling processing of CreditController");
		return creditParseService.processCsvData();
	}
	
	
	
	//******************************** Searching functions *********************************
    @GetMapping("/search")
    public Page<Credit> searchCredits(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Integer minCreditScore,
            @RequestParam(required = false) Integer maxCreditScore,
            @RequestParam(required = false) String homeOwnership,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
    	log.info("calling searchCredits of CreditController");
        Pageable pageable = PageRequest.of(page,size);
        return creditParseService.search(term,minCreditScore,maxCreditScore,homeOwnership,pageable);
    }
    
    @GetMapping("/{id}")
    public Credit getCredit(@PathVariable("id") String id) {
    	return creditParseService.findByLoanId(id);
    }
    
    
    
    

    

}
