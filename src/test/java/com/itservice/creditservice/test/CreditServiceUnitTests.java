package com.itservice.creditservice.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.itservice.creditservice.Exception.CreditServiceException;
import com.itservice.creditservice.collection.Credit;
import com.itservice.creditservice.repository.CreditRepository;
import com.itservice.creditservice.service.CreditService;
import com.itservice.creditservice.service.CreditServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreditServiceUnitTests {
    @Mock
    private CreditRepository creditRepository;
    
    @InjectMocks
    private CreditServiceImpl creditService;

    @Test
    void getCredit() {
        Credit credit = Credit.builder()
        		.loanId("0b2f1b66-741e-4e37-a929-99926cdc9e9a")
        		.customerId("6a1adeda-079b-49e5-ac7c-91828f2806a0")
        		.annualIncome(Double.valueOf("234234"))
        		.build();
        when(creditRepository.findById(any())).thenReturn(Optional.of(credit));

        Credit creditResult = creditService.findByLoanId("0b2f1b66-741e-4e37-a929-99926cdc9e9a");

        assertThat(creditResult.getCustomerId()).isEqualTo("6a1adeda-079b-49e5-ac7c-91828f2806a0");
        assertThat(creditResult.getAnnualIncome()).isEqualTo(234234);
    }

//    @Test
//    void parseNullFile() {
//    	
//    	creditService.parseSaveCsvFile(null);
//        assertThrows(CreditServiceException.class, () -> creditService.parseSaveCsvFile(null), "Get empty file");
//    }
}
