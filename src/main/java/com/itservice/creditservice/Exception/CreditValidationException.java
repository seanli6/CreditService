package com.itservice.creditservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class CreditValidationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
	
	public CreditValidationException(String message) {
		super(message);
	}
}
