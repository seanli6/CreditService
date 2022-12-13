package com.itservice.creditservice.test;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.controller.CreditController;
import com.itservice.creditservice.exception.CreditServiceException;
import com.itservice.creditservice.exception.CreditValidationException;
import com.itservice.creditservice.service.CreditService;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditController.class)
class CreditControllerTests {
	@MockBean
	private CreditService creditService;
	
	@MockBean
	private KafkaReceiver<String,String> kafkaReceiver;
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	void findCreditByLoanId() throws Exception {
		Credit credit = Credit.builder().loanId("0b2f1b66-741e-4e37-a929-99926cdc9e9a")
				.customerId("6a1adeda-079b-49e5-ac7c-91828f2806a0").creditScore(Integer.valueOf(650))
				.annualIncome(Double.valueOf("234234")).build();
		when(creditService.findByLoanId(any())).thenReturn(credit);
		
		when(kafkaReceiver.receive(any())).thenReturn(null);

		Credit creditResult = creditService.findByLoanId("0b2f1b66-741e-4e37-a929-99926cdc9e9a");

		assertThat(creditResult.getCustomerId()).isEqualTo("6a1adeda-079b-49e5-ac7c-91828f2806a0");
		assertThat(creditResult.getAnnualIncome()).isEqualTo(234234);

		mockMvc.perform(get("/credits/{id}", "0b2f1b66-741e-4e37-a929-99926cdc9e9a").header("Authorization",
				TestUtils.authorizationHeader)).andExpect(jsonPath("$.creditScore").value(650))
				.andExpect(jsonPath("$.annualIncome").value("234234.0"))
				.andExpect(jsonPath("$.customerId").value("6a1adeda-079b-49e5-ac7c-91828f2806a0"));
	}

	@Test
	void getExceptionInCreditsSearch() throws Exception {
		when(creditService.search(any(), any(), any(), any(), any())).thenThrow(CreditServiceException.class);
		mockMvc.perform(
				get("/credits/search?term=Long Term&minCreditScore=600&maxCreditScore=700&homeOwnership=&page=&size=")
						.header("Authorization", TestUtils.authorizationHeader))
				.andExpect(status().isBadRequest());
	}

	@Test
	void uploadEmptyFile() throws Exception {
		when(kafkaReceiver.receive(any())).thenReturn(null);

		doThrow(new CreditServiceException("Get empty file")).when(creditService).prepareCsvData(any());

		mockMvc.perform(post("/credits/upload?file=").header("Authorization", TestUtils.authorizationHeader)
				.contentType(MediaType.MULTIPART_FORM_DATA).content("{\"file\": \"4532756279624064\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void invalidCsvFile() throws Exception {
		
		when(kafkaReceiver.receive(any())).thenReturn(null);

		doThrow(new CreditValidationException("Get empty file")).when(creditService).prepareCsvData(any());

		mockMvc.perform(post("/credits/upload?file=").header("Authorization", TestUtils.authorizationHeader)
				.contentType(MediaType.MULTIPART_FORM_DATA).content("{\"file\": \"4532756279624064\"}"))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void uploadFileAndParseCsv() throws Exception {
		
		when(kafkaReceiver.receive(any())).thenReturn(null);

		mockMvc.perform(post("/credits/upload?file=").header("Authorization", TestUtils.authorizationHeader)
				.contentType(MediaType.MULTIPART_FORM_DATA).content("{\"file\": \"4532756279624064\"}"))
				.andExpect(status().isOk());
	}
}