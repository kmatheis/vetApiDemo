package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.kmatheis.vet.controller.support.ModifyUsersTestSupport;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
public class ModifyUsersTest extends ModifyUsersTestSupport {

	@Test
	void testAttemptToDeleteSuperuser() {
		// Given: admin credentials
		// When: that admin logs in and attempts to delete the superuser
	    HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s/1", getBaseUriForUsers() );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), String.class );
		
		// Then: we return BAD REQUEST (400)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
	}
	
	@Test
	void testAttemptToAddSuperuser() {
		// Given: admin credentials
		// When: that admin logs in and attempts to add an existing user
	    HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"vetroot\", \"password\": \"rootedtree\", \"rolename\": \"ADMIN\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		
		// Then: we return BAD REQUEST (400)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
	}
	
	@Test
	void testAddAdmin() {
		// Given: admin credentials
		// When: that admin logs in and adds an admin
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"anadmin\", \"password\": \"anadmin\", \"rolename\": \"ADMIN\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		// Then: the add is successful.
		assertThat( response.getBody() ).isEqualTo( "Successfully added user." );
	}
	
	@Test
	void testAddReceptionist() {
		// Given: admin credentials
		// When: that admin logs in and adds a receptionist
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"arec\", \"password\": \"arec\", \"rolename\": \"RECEPTIONIST\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		// Then: the add is successful.
		assertThat( response.getBody() ).isEqualTo( "Successfully added user." );
	}
	
	@Test
	void testAddTechnician() {
		// Given: admin credentials
		// When: that admin logs in and adds a technician
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"atech\", \"password\": \"atech\", \"rolename\": \"TECHNICIAN\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		// Then: the add is successful.
		assertThat( response.getBody() ).isEqualTo( "Successfully added user." );
	}
	
	@Test 
	void testRecAddingRec() {
		// Given: receptionist credentials
		// When: that rec logs in and attempts to add a rec
	    HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"arec\", \"password\": \"arec\", \"rolename\": \"RECEPTIONIST\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		
		// Then: we return UNAUTHORIZED (401)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.UNAUTHORIZED );
	}
}
