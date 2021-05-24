package com.kmatheis.vet.controller.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

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
	
}
