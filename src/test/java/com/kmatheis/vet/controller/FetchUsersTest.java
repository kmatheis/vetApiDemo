package com.kmatheis.vet.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.kmatheis.vet.controller.support.FetchUsersTestSupport;
import com.kmatheis.vet.entity.UserReply;


// @Nested  // When JUnit 5 finds this, will create a new application context.
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class FetchUsersTest extends FetchUsersTestSupport {

	@Test
	void test() {
		String body = "{ \"username\": \"vetroot\", \"password\": \"root\" }";
		String uri = getBaseUriForUsers() + "/login";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		
		// now assert things
	}

}



