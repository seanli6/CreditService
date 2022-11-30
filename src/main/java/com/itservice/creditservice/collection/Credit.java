package com.itservice.creditservice.collection;



import java.sql.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document(collection = "credit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Credit {
	
	@Id
	private String loanId;
	private String customerId;
	private Double currentLoanAmt;
	private String term;
	private Integer creditScore;
	private Double annualIncome;
	private String yearsInJob;
	private String homeOwnership;
	private String purpose;
	private Double monthlyDebt;
	private Double yearsOfCredit;
	private Double currentCreditBal;
	private Double maxOpenCredit;

}
