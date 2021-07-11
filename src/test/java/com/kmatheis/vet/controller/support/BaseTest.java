package com.kmatheis.vet.controller.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kmatheis.vet.internal.UserReply;

import lombok.Getter;

public class BaseTest {
	
	@LocalServerPort
	private int serverPort;  // Spring fills this in for us thanks to the annotation
	
	@Autowired
	@Getter  // thanks to Lombok, a getter has been created, getRestTemplate(), so we don't have to pollute our code (check Outline)
	protected TestRestTemplate restTemplate; 
	
	protected String getBaseUriForUsers() {  // URI of the request that will be sent to the application
		return String.format( "http://localhost:%d/users" , serverPort );
	}
	
	protected String getBaseUriForSomeUsers() {  // URI of the request that will be sent to the application
		return String.format( "http://localhost:%d/someusers" , serverPort );
	}
	
//	protected String obtainJwtFromValidLogin( String username, String password ) {  // Deprecated but remains for reference
//		String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
//		String uri = getBaseUriForUsers() + "/loginold";
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType( MediaType.APPLICATION_JSON );
//		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
//		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
//		return response.getBody().getJwt();
//	}
	
	protected String obtainJwtFromValidLoginAuthHeader( String username, String password ) {
		String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
		String uri = getBaseUriForUsers() + "/login";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		return response.getHeaders().get( "Authorization" ).get( 0 );
	}	
	
	// Logs in the user and returns correct headers to be passed to a subsequent exchange() call.
	protected HttpHeaders obtainHeadersFromValidLogin( String username, String password ) {
		String jwt = obtainJwtFromValidLoginAuthHeader( username, password );
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		// headers.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );
	    headers.add( "User-Agent", "Spring's RestTemplate" );  // value can be whatever
	    headers.add( "Authorization", "Bearer " + jwt );
	    return headers;
	}
	
	protected void assertErrorMessageValid( Map<String, Object> error, HttpStatus status ) {
		assertThat( error )
			.containsKey( "message" )
			.containsEntry( "status code", status.value() )
			.containsEntry( "uri", "/jeeps" )
			.containsKey( "timestamp" )
			.containsEntry( "reason", status.getReasonPhrase() );
	}
	
}
