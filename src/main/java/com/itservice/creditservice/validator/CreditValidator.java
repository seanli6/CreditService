package com.itservice.creditservice.validator;

import org.apache.commons.csv.CSVRecord;

import com.itservice.creditservice.Exception.CreditValidationException;
import com.itservice.creditservice.constant.HeaderName;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CreditValidator {

	public static void validateCreditInfo(CSVRecord csvRrd) {

		log.info("Calling validateCreditInfo of CreditValidator");

		try {
			// Loan id cannot be null
			String loanId = csvRrd.get(HeaderName.LOANID.getDesp());
			if (StringUtils.isBlank(loanId)) {
				log.error("Loan Id is empty");
				throw new CreditValidationException("Loan Id is empty");
			}
		} catch (IllegalArgumentException e) {
			log.error("Get parsing error.");
			throw new CreditValidationException("Get parsing error.");
		}
	}
}
