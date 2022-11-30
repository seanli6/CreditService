package com.itservice.creditservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itservice.creditservice.collection.Credit;



public interface CreditService {
	
	void parseSaveCsvFile(MultipartFile file);
	
	Page<Credit> search(String term, Integer minCreditScore, Integer maxCreditScore, String homeOwnership, Pageable pageable);
	
	Credit findByLoanId(String loanId);
}
