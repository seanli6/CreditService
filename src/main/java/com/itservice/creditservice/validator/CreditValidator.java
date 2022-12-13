package com.itservice.creditservice.validator;

import org.apache.commons.csv.CSVRecord;

import com.itservice.creditservice.constant.HeaderName;
import com.itservice.creditservice.exception.CreditValidationException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Slf4j
public class CreditValidator {

	public static void validateCreditInfo(CSVRecord csvRrd) throws CreditValidationException {

		//log.info("Calling validateCreditInfo of CreditValidator");

		try {
			// Loan id cannot be null
			String loanId = csvRrd.get(HeaderName.LOANID.getDesp());
			if (StringUtils.isBlank(loanId)) {
				log.error("Loan Id is empty");
				throw new CreditValidationException("Loan Id is empty");
			}
			
			//Credit score has to be a number
			String creditScore = csvRrd.get(HeaderName.CREDITSCORE.getDesp());
			if (StringUtils.isNotBlank(creditScore) && NumberUtils.isNumber(creditScore) == false) {
				log.error("Credit Score is not a number");
				throw new CreditValidationException("Credit Score is not a number");
			}
			
			//Annual income has to be a number
			String annualIncome = csvRrd.get(HeaderName.ANNUALINC.getDesp());
			if (StringUtils.isNotBlank(annualIncome) && NumberUtils.isNumber(annualIncome) == false) {
				log.error("Annual income is not a number");
				throw new CreditValidationException("Annual income is not a number");
			}
			
			//monthly Debt has to be a number
			String monthlyDebt = csvRrd.get(HeaderName.MONDEBT.getDesp());
			if (StringUtils.isNotBlank(monthlyDebt) && NumberUtils.isNumber(monthlyDebt) == false) {
				log.error("Monthly Debt is not a number");
				throw new CreditValidationException("Monthly Debt is not a number");
			}
			
			//Current Credit Balance has to be a number
			String currentBalance = csvRrd.get(HeaderName.CURCRETBAL.getDesp());
			if (StringUtils.isNotBlank(currentBalance) && NumberUtils.isNumber(currentBalance) == false) {
				log.error("Current Credit Balance is not a number");
				throw new CreditValidationException("Current Credit Balance is not a number");
			}
		} catch (IllegalArgumentException e) {
			log.error("Get parsing error.");
			throw new CreditValidationException("Get parsing error.");
		}
	}
}
