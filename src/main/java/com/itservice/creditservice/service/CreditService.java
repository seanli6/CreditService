package com.itservice.creditservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.itservice.creditservice.collection.Credit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface CreditService {
	
	void parseSaveCsvFile(MultipartFile file);
	
	Page<Credit> search(String term, Integer minCreditScore, Integer maxCreditScore, String homeOwnership, Pageable pageable);
	
	Credit findByLoanId(String loanId);

	//Use WebFlux solution
	Mono<String> prepareCsvData(MultipartFile file);
	Flux<String> processCsvData();
	
	//Use Kafka solution: use sessionId as the key which we pass to kafka
	Mono<String> prepareAndProcessCsvData(MultipartFile file, String key);
}
