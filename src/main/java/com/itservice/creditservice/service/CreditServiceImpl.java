package com.itservice.creditservice.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.exception.CreditServiceException;
import com.itservice.creditservice.exception.CreditValidationException;
import com.itservice.creditservice.repository.CreditRepository;
import com.itservice.creditservice.util.MyUtils;
import com.itservice.creditservice.validator.CreditValidator;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {

	@Autowired
	CreditRepository creditRepository;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
    ProcessingMsgProducer kafkaProducer;

	private static final int GROUPSIZE = 500;
	
	private List<Credit> validatedCrdLst = new ArrayList<>();
	private long originalCrdSize = 0;
	
	@Override
	public Mono<String> prepareCsvData(MultipartFile file) {
		log.info("calling prepareCsvData of CreditServiceImpl");
		
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
		
		//Reset credit list and the original size
		validatedCrdLst = new ArrayList<>();
		originalCrdSize = 0;
		
		try {
			Reader reader = new InputStreamReader(file.getInputStream());

			// Create CSVParser
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			List<CSVRecord> records = csvParser.getRecords();
			originalCrdSize = records.size();
			
			// Loop the records
			records.forEach(c -> validatedCrdLst.add(processCsvRecord(c)));
			// Close CSVParser
			csvParser.close();

		} catch (IOException e) {
			log.error("Got IOException when loading csv file.");
			throw new CreditServiceException("Got IOException when loading csv file.");
		}
		return Mono.just("Successfully uploaded csv file!");
		
    }
	
	@Override
    public Flux<String> processCsvData() {
    	
		//log.info("calling processCsvData of CreditServiceImpl");
			
			//get the size of csv
	    	int credListSize = validatedCrdLst.size();
	    	
	    	//if the validated credit list size is smaller than original size, means there are validation errors
			if(credListSize > 0 && credListSize == originalCrdSize) {
				
				//Determine how many groups need to be processed here
		    	int groupNum = (int)Math.ceil((double)credListSize/GROUPSIZE);
		    	
				return Flux.range(1,groupNum)
//	            .delayElements(Duration.ofSeconds(1))
	            .doOnNext(i -> {
	            	log.info("processing count in stream flow : " + i);
	            	
	            	List<Credit> subCrtList = null;
	            	//Deal with the last group of leftover records 
		    		if(validatedCrdLst.size()<GROUPSIZE) {
		    			subCrtList = validatedCrdLst.subList(0 , validatedCrdLst.size());
		    		} 
		    		//For the group of 0 - BASE_NUM
		    		else {
		    			subCrtList = validatedCrdLst.subList(0 , GROUPSIZE);
		    		}
		    		
		    		//Save group of records into repository
	            	creditRepository.saveAll(subCrtList);

	            	})
	            .map(i -> {
	            	//Return processing result message as string
	            	StringBuffer msg = new StringBuffer("Data ");
	            	
	            	List<Credit> subCrtList = null;
	            	//Deal with the last group of leftover records 
		    		if(validatedCrdLst.size()<GROUPSIZE) {
		    			subCrtList = validatedCrdLst.subList(0 , validatedCrdLst.size());
	            		msg.append((i-1)*GROUPSIZE + " - " + credListSize);
		    		} 
		    		//For the group of 0 - BASE_NUM
		    		else {
		    			subCrtList = validatedCrdLst.subList(0 , GROUPSIZE);
	            		msg.append((i-1)*GROUPSIZE + " - " + i*GROUPSIZE);
		    		}
	            	//once data processing is done
	            	subCrtList.clear();
	            	
	            	msg.append(" are saved.");

	            	return msg.toString();
	            });
			} else {
				return Flux.just("Waiting for uploading csv file...");
			}
    }
    
	
	@Override
    public Mono<String> prepareAndProcessCsvData(MultipartFile file, String key) {
		log.info("calling prepareAndProcessCsvData of CreditServiceImpl");
		
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
		
		
		try {
			Reader reader = new InputStreamReader(file.getInputStream());

			// Create CSVParser
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			List<CSVRecord> records = csvParser.getRecords();
			List<Credit> creditList = new ArrayList<>();
			// Loop the records and save to a list
			records.forEach(c -> creditList.add(processCsvRecord(c)));
			int credListSize = creditList.size();
			
			if(credListSize > 0 ) {
				
				//Determine how many groups need to be processed here
		    	int groupNum = (int)Math.ceil((double)credListSize/GROUPSIZE);
		    	
				IntStream.range(1,groupNum+1)
	            .forEach(i -> {
	            	log.info("processing count in stream flow : " + i);
	            	//MyUtils.sleep(1);
	            	
	            	StringBuffer msg = new StringBuffer("Data ");
	            	List<Credit> subCrtList = null;
	            	
	            	//Deal with the last group of leftover records 
		    		if(creditList.size()<GROUPSIZE) {
		    			subCrtList = creditList.subList(0 , creditList.size());
	            		msg.append((i-1)*GROUPSIZE + " - " + credListSize);
		    		} 
		    		//For the group of 0 - BASE_NUM
		    		else {
		    			subCrtList = creditList.subList(0 , GROUPSIZE);
	            		msg.append((i-1)*GROUPSIZE + " - " + i*GROUPSIZE);
		    		}
		    		
		    		msg.append(" are saved.");
		    		
		    		//Save group of records into repository
	            	creditRepository.saveAll(subCrtList);
	            	
	            	//once data processing is done, clear the sub list
	            	subCrtList.clear();
	            	
	            	//produce the message and senf to topic
	            	kafkaProducer.sendMessageToTopic(msg.toString(), key);

	            });
	            
			} else {
//				return Flux.just("Waiting for uploading csv file...");
				kafkaProducer.sendMessageToTopic("Waiting for uploading csv file...", null);
			}
			
			// Close CSVParser
			csvParser.close();

		} catch (IOException e) {
			log.error("Got IOException when loading csv file.");
			throw new CreditServiceException("Got IOException when loading csv file.");
		}
		return Mono.just("Successfully uploaded csv file!");
		
    }
    
	@Override
	public void parseSaveCsvFile(MultipartFile file){
		log.info("calling parseSaveCsvFile of CreditServiceImpl");
		try {

			List<Credit> creditList = new ArrayList<>();

			// Read the file
			Reader reader = new InputStreamReader(file.getInputStream());

			// Create CSVParser
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			
			// Loop the records
			csvParser.forEach(c -> creditList.add(processCsvRecord(c)));

			// Close CSVParser
			csvParser.close();
			
			// Mark the start time
			Instant start = Instant.now();
			creditRepository.saveAll(creditList);
			// Mark the end time
			Instant end = Instant.now();
			
			// Log the time was consuming in creditRepository.save(credit);
			Duration timeElapsed = Duration.between(start, end);
			log.info("Time taken: for save():" + timeElapsed.toMillis() + " milliseconds");
			log.info("Finish parsing and saving data");
		} catch (IOException e) {
			log.error("Got IOException when loading csv file.");
			throw new CreditServiceException("Got IOException when loading csv file.");
		}

	}

	private Credit processCsvRecord(CSVRecord c) {

		//log.info("calling processCsvRecord of CreditServiceImpl");

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
	
	
    
    public static void main(String[] args) {
    	List<Integer> range = IntStream.range(1, 1956).boxed().collect(Collectors.toList());
    	int BASE_NUM = 500;
    	
    	double x = (double)range.size()/BASE_NUM;
    	
    	System.out.println(x);

    	System.out.println(Math.round(x));
    	//Rounds to nearest int
    	System.out.println(Math.ceil(x));
    	//Rounds up to int
    	System.out.println(Math.floor(x));
    	//Rounds down to int
    	

//		List<Integer> sub = range.subList(2000 , 2100);
//		System.out.println(sub.size());
    	
    	int groupNum = (int)Math.ceil((double)range.size()/BASE_NUM);
    	System.out.println(groupNum);
    	
    	for(int i=0; i<groupNum; i++) {
//    		System.out.println((i-1)*50 + " / "+ i*50);
    		List<Integer> rangeSub = range.subList(0 , BASE_NUM);
    		System.out.println(rangeSub);
    		rangeSub.clear();
//    		System.out.println(rangeSub.size());
    		System.out.println("range size "+range.size());
    		
    		if(range.size()<BASE_NUM) {
    			BASE_NUM = range.size();
    		}
    	}
    }

}
