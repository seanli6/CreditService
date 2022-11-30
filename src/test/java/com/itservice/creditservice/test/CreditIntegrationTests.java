package com.itservice.creditservice.test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.assertj.core.api.Assertions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.constant.HeaderName;
import com.itservice.creditservice.repository.CreditRepository;
import com.itservice.creditservice.util.MyUtils;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CreditIntegrationTests {
	
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private CreditRepository creditRepository;

//    @BeforeAll
//    static void setupMockWebServer() throws Exception {
//    	initialTestingData();
//    }

//    @AfterEach
//    void deleteEntities() {
//    	creditRepository.deleteAll();
//    }
    
    @Test
    @Order(1)
    void loadCsvData() throws Exception {
    	initialTestingData();
    }
    

    @Test
    @Order(2)
    void findCreditByLoanId() throws Exception {

    	webClient.get().uri("/credits/{id}", "0b2f1b66-741e-4e37-a929-99926cdc9e9a")
    	.header("Authorization", TestUtils.authorizationHeader)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.loanId").isNotEmpty()
		.jsonPath("$.loanId").isEqualTo("0b2f1b66-741e-4e37-a929-99926cdc9e9a")
		.jsonPath("$.customerId").isEqualTo("6a1adeda-079b-49e5-ac7c-91828f2806a0")
		.jsonPath("$.creditScore").isEqualTo("736")
		.jsonPath("$.annualIncome").isEqualTo("1068617.0");
    }
    
    
    @Test
    @Order(3)
    void searchByCriteria() throws Exception {

    	webClient.get().uri("/credits/search?term=Short Term&minCreditScore=600&maxCreditScore=800&homeOwnership=Rent&page=&size=")
    	.header("Authorization", TestUtils.authorizationHeader)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.content").isNotEmpty()
		.jsonPath("$.totalElements").isEqualTo("7");
    	
//		.consumeWith(System.out::println);
    }
    
  
    

//    
//    @Test
    void anotherTest() throws Exception{
    	File file = new File("sample.csv");
    	FileInputStream input = new FileInputStream(file);
    	MultipartFile multipartFile = new MockMultipartFile("file",input);
    	
  	  String name = "file.csv";
  	  String originalFileName = "file.csv";
  	  String contentType = "text/csv";

    	Path path = Paths.get("sample.csv");
    	byte[] content = Files.readAllBytes(path);
    	MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

    	
    	
    	webClient.post().uri("/credits")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(multipartFile), MultipartFile.class)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.consumeWith(response -> {
			Assertions.assertThat(response.getResponseBody()).isNotNull();
	});
    
    }

    
    
	private void initialTestingData() throws Exception {
		
		
		//Read the file
    	File file = new File("sample.csv");
    	FileInputStream input = new FileInputStream(file);
		Reader reader = new InputStreamReader(input);
		
		//Create CSVParser
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
		Instant start = Instant.now();
		
		//Loop the records
		csvParser.forEach(c -> {
			//Get the data out of each CSVRecord and populate them to Credit Object
			creditRepository.save(MyUtils.parseCsvRecord(c));
		});
		
		//Close CSVParser
		csvParser.close();
	}
}