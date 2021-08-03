package com.kmatheis.vet.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	private enum LogStatus { STACK_TRACE, MESSAGE_ONLY };
	
	@ExceptionHandler( NoSuchElementException.class )
	@ResponseStatus( code = HttpStatus.NOT_FOUND )  // i.e., 404
	public Map<String, Object> handleNoSuchElementException( NoSuchElementException e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	@ExceptionHandler( AuthenticationException.class )
	@ResponseStatus( code = HttpStatus.UNAUTHORIZED )  // i.e., 401
	public Map<String, Object> handleAuthenticationException( AuthenticationException e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.UNAUTHORIZED, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	@ExceptionHandler( IllegalAttemptException.class )
	@ResponseStatus( code = HttpStatus.BAD_REQUEST )  // i.e., 400
	public Map<String, Object> handleIllegalAttemptException( IllegalAttemptException e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	@ExceptionHandler( DuplicateKeyException.class )
	@ResponseStatus( code = HttpStatus.BAD_REQUEST )  // i.e., 400
	public Map<String, Object> handleDuplicateKeyException( DuplicateKeyException e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	// For when user provides bad JSON such as incorrect enums.
	@ExceptionHandler( HttpMessageNotReadableException.class )
	@ResponseStatus( code = HttpStatus.BAD_REQUEST )  // i.e., 400
	public Map<String, Object> handleHttpMessageNotReadableException( HttpMessageNotReadableException e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY );
	}
	
	private String getLastDefaultMessage( String s ) {
		int i = s.indexOf( "default message" );
		boolean found = false;
		while ( i >= 0 ) {
			found = true;
			s = s.substring( i + 1 );
			i = s.indexOf( "default message" );
			// System.out.println( s );
		}
		if ( !found ) {
			return s;
		}
		int lbi = s.indexOf( "[" );
		int rbi = s.indexOf( "]" );
		return s.substring( lbi + 1, rbi );
	}
	
	@ExceptionHandler( MethodArgumentNotValidException.class )
	@ResponseStatus( code = HttpStatus.BAD_REQUEST )  // i.e., 400
	public Map<String, Object> handleMethodArgumentNotValidException( MethodArgumentNotValidException e, WebRequest webRequest ) {
		// If a bean validator triggers, the message in the error natively is a bit verbose.
		// Thus we need to parse it a bit to extract only the message we provided in the @Min, @Max., etc. annotation.
		// This message occurs as the last "default message" bit in the error text.
		return createExceptionMessage( e, HttpStatus.BAD_REQUEST, webRequest, LogStatus.MESSAGE_ONLY, getLastDefaultMessage( e.toString() ) );
	}
	
	// Handle any exception that is not covered by the above.
	@ExceptionHandler( Exception.class )
	@ResponseStatus( code = HttpStatus.INTERNAL_SERVER_ERROR )  // i.e., 500
	public Map<String, Object> handleException( Exception e, WebRequest webRequest ) {
		return createExceptionMessage( e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, LogStatus.STACK_TRACE );
	}
	
	private Map<String, Object> createExceptionMessage( Exception e, HttpStatus status, WebRequest webRequest, LogStatus logStatus ) {
		return createExceptionMessage( e, status, webRequest, logStatus, e.toString() );  // toString() will also typ. provide class, for better or worse
	}
	
	private Map<String, Object> createExceptionMessage( Exception e, HttpStatus status, WebRequest webRequest, LogStatus logStatus, String msg ) {
		Map<String, Object> error = new HashMap<>();
		String timestamp = ZonedDateTime.now().format( DateTimeFormatter.RFC_1123_DATE_TIME );
		
		if ( webRequest instanceof ServletWebRequest ) {
			error.put( "uri", ((ServletWebRequest) webRequest).getRequest().getRequestURI() );
		}
		
		error.put( "message", msg );
		error.put( "status code", status.value() );
		error.put( "timestamp", timestamp );
		error.put( "reason", status.getReasonPhrase() );
		
		if ( logStatus == LogStatus.MESSAGE_ONLY ) {
			log.error( "Exception: {}", e.toString() );  // logs just the type and the message
		} else {
			log.error( "Exception: ", e );             // logs the entire stack trace
		}
		
		return error;
	}
}
