package com.itservice.creditservice.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itservice.creditservice.Exception.CreditServiceException;
import com.itservice.creditservice.Exception.CreditValidationException;
import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.constant.HeaderName;
import com.itservice.creditservice.controller.CreditController;
import com.itservice.creditservice.repository.CreditRepository;
import com.itservice.creditservice.util.MyUtils;
import com.itservice.creditservice.validator.CreditValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {

	@Autowired
	CreditRepository creditRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void parseSaveCsvFile(MultipartFile file) {
		log.info("calling parseSaveCsvFile of CreditServiceImpl");
		try {

			// Handle the exceptions if no file or invalid file is passed in
			if (file == null || file.getContentType() == null) {
				log.error("Get empty file");
				throw new CreditServiceException("Get empty file");
			}
			// If pass in file is not a csv type, throw exception
			else if (file.getContentType().contains("csv") == false) {
				log.error("File is not csv type. File type is: file.getContentType()");
				throw new CreditServiceException("File is not csv type");
			}

			List<Credit> creditList = new ArrayList<>();

			// Read the file
			Reader reader = new InputStreamReader(file.getInputStream());

			// Create CSVParser
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			
			// Mark the start time
			Instant start = Instant.now();

			// Loop the records
			csvParser.forEach(c -> {
				Credit credit = processCsvRecord(c);
				creditRepository.save(credit);
				creditList.add(credit);
			});

			// Close CSVParser
			csvParser.close();
			
			// Mark the end time
			Instant end = Instant.now();
			
			// Log the time was consuming in creditRepository.save(credit);
			Duration timeElapsed = Duration.between(start, end);
			log.info("Time taken: for save():" + timeElapsed.toMillis() + " milliseconds");
			log.info("Finish parsing and saving data");

//			Instant start2 = Instant.now();
//			creditRepository.saveAll(creditList);
//			Instant end2 = Instant.now();
//			Duration timeElapsed2 = Duration.between(start2, end2);
//			log.info("*** Time taken: for saveAll():" + timeElapsed2.toMillis() + " milliseconds");
		} catch (IOException e) {
			log.error("Got IOException when loading csv file.");
			throw new CreditServiceException("Got IOException when loading csv file.");
		}

	}

	private Credit processCsvRecord(CSVRecord c) {

		log.info("calling processCsvRecord of CreditServiceImpl");

		// Validate CSVRecord
		CreditValidator.validateCreditInfo(c);
		Credit credit = null;
		try {
			// Get the data out of each CSVRecord and populate them to Credit Object
			credit = MyUtils.parseCsvRecord(c);
		} catch (Exception e) {
			log.error("Get parsing error.");
			throw new CreditValidationException("Get parsing error.");
		}
		return credit;
	}
	
	/*
	 * search credit based on criteria term, minCreditScore, maxCreditScore, homeOwnership
	 * 
	 * This method implements the Pageable function: Pagination is helpful here because we have a large dataset 
	 * and we want to present it to the user in smaller chunks (User can define the size of page). Also, we often 
	 * need to sort that data by some criteria while paging.
	 * 
	 */
	@Override
	public Page<Credit> search(String term, Integer minCreditScore, Integer maxCreditScore, String homeOwnership,
			Pageable pageable) {

		log.info("calling search of CreditServiceImpl");

		// Create Query and list of Criteria with pageable
		Query query = new Query().with(pageable);
		List<Criteria> criteria = new ArrayList<>();

		// Searching by term
		if (StringUtils.isNotBlank(term)) {
			criteria.add(Criteria.where("term").regex(term, "i"));
		}

		// Searching the range of credit score
		if (minCreditScore != null && maxCreditScore != null) {
			criteria.add(Criteria.where("creditScore").gte(minCreditScore).lte(maxCreditScore));
		}

		// Searching by homeOwnership
		if (StringUtils.isNotBlank(homeOwnership)) {
			criteria.add(Criteria.where("homeOwnership").is(homeOwnership));
		}

		if (!criteria.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
		}

		// Search by using Page
		Page<Credit> credit = PageableExecutionUtils.getPage(mongoTemplate.find(query, Credit.class), pageable,
				() -> mongoTemplate.count(query.skip(0).limit(0), Credit.class));

		return credit;
	}
	
	/*
	 * Find credit by loanId
	 */
	@Override
	public Credit findByLoanId(String loanId) {
		return creditRepository.findById(loanId).get();
	}

}
