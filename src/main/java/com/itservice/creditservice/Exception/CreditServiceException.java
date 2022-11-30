package com.itservice.creditservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CreditServiceException extends RuntimeException {
	

	private static final long serialVersionUID = 1L;

	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
	
	public CreditServiceException(String message) {
		super(message);
	}
}
