package com.itservice.creditservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/credits")
@Slf4j
public class CreditController {
	
	@Autowired
	CreditService creditParseService;
	
	@PostMapping()
	public String loadAndParseCsv(@RequestBody MultipartFile file) {
		log.info("calling loadAndParseCsv of CreditController");
		creditParseService.parseSaveCsvFile(file);
		return "Successfully parsed the csv file and saved the data";
	}
	
	
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
