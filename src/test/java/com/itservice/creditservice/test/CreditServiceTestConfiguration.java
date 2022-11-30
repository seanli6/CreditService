package com.itservice.creditservice.test;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.itservice.creditservice.service.CreditService;


@Profile("test")
@Configuration
public class CreditServiceTestConfiguration {
   @Bean
   @Primary
   public CreditService productService() {
      return Mockito.mock(CreditService.class);
   }
}