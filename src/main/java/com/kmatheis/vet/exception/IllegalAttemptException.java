package com.kmatheis.vet.exception;

@SuppressWarnings( "serial" )
public class IllegalAttemptException extends RuntimeException {

	public IllegalAttemptException( String message ) {
		super( message );
	}
}
