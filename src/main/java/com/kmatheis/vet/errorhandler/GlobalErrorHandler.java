package com.kmatheis.vet.errorhandler;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	private enum LogStatus { STACK_TRACE, MESSAGE_ONLY };

	@ExceptionHandler( NoSuchElementException.class )
	@ResponseStatus( code = HttpStatus.NOT_FOUND )  // i.e., 404
	public Map<String, Object> handleNoSuchElementException( NoSuchElementException e, WebRequest webRequest ) {
		return null;
		// return createExceptionMessage( e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	@ExceptionHandler( AuthenticationException.class )
	@ResponseStatus( code = HttpStatus.UNAUTHORIZED )  // i.e., 401
	public Map<String, Object> handleAuthenticationException( AuthenticationException e, WebRequest webRequest ) {
		return null;
		// return createExceptionMessage( e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY );
	}
}
