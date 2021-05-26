package com.kmatheis.vet.controller.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.kmatheis.vet.entity.UserReply;

import lombok.Getter;

public class BaseTest {
	
	@LocalServerPort
	private int serverPort;  // Spring fills this in for us thanks to the annotation
	
	@Autowired
	@Getter  // thanks to Lombok, a getter has been created, getRestTemplate(), so we don't have to pollute our code (check Outline)
	protected TestRestTemplate restTemplate;  // change from private (Lecture) to protected (hw)
	
	protected String getBaseUriForUsers() {  // URI of the request that will be sent to the application
		return String.format( "http://localhost:%d/users" , serverPort );
	}
	
	protected String obtainJwtFromValidLogin( String username, String password ) {
		String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";
		String uri = getBaseUriForUsers() + "/login";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		return response.getBody().getJwt();
	}
	
	protected HttpEntity<String> obtainBodyEntityFromJwt( String jwt ) {
		String body = "{ \"jwt\": \"" + jwt + "\" }";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		return new HttpEntity<>( body, headers );
	}
	
	protected HttpEntity<String> obtainJwtBodyEntityFromValidLogin( String username, String password ) {
		return obtainBodyEntityFromJwt( obtainJwtFromValidLogin( username, password ) );
	}
	
}
