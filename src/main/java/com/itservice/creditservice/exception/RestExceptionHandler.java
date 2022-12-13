package com.itservice.creditservice.exception;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.itservice.creditservice.model.ErrorResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice()
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CreditServiceException.class)
    public ResponseEntity<Object> handleCreditServiceException(HttpServletRequest req,CreditServiceException ex){
        return buildResponseEntity(ErrorResponse.builder()
        		.message(ex.getMessage())
        		.status(HttpStatus.BAD_REQUEST)
        		.path(req.getRequestURI())
        		.timeStamp(LocalDateTime.now())
        		.build());
    }

    
    @ExceptionHandler(CreditValidationException.class)
    public ResponseEntity<Object> handleCreditValidationException(HttpServletRequest req,CreditValidationException ex){
        return buildResponseEntity(ErrorResponse.builder()
        		.message(ex.getMessage())
        		.status(HttpStatus.UNPROCESSABLE_ENTITY)
        		.path(req.getRequestURI())
        		.timeStamp(LocalDateTime.now())
        		.build());
    }

    

    private ResponseEntity<Object> buildResponseEntity(ErrorResponse errorResponse){
        return new ResponseEntity<Object>(errorResponse, errorResponse.getStatus());
    }

}
