package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
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

import com.kmatheis.vet.controller.support.FetchUsersTestSupport;
import com.kmatheis.vet.entity.ServerKey;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.entity.UserReply;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;


// @Nested  // When JUnit 5 finds this, will create a new application context.
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class FetchUsersTest extends FetchUsersTestSupport {

	@Test
	void testValidSuperuserLogin() {
		// Given: valid login credentials
		String body = "{ \"username\": \"vetroot\", \"password\": \"root\" }";
		String uri = getBaseUriForUsers() + "/login";
		
		// When: the user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		
		// Then: we return an OK status along with a JWT
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		UserReply actual = response.getBody();
		assertThat( actual.getUser().getUsername() ).isEqualTo( "vetroot" );
		assertThat( actual.getUser().getRoleId() ).isEqualTo( 1 );
		String[] parts = actual.getJwt().split( "\\." );  // since input is interpreted as a regex
		assertThat( parts ).hasSize( 3 );		         
					         
		Claims claims = Jwts.parser().setSigningKey( ServerKey.workingKey ).parseClaimsJws( actual.getJwt() ).getBody();
		assertThat( claims.getSubject() ).isEqualTo( "VET API DEMO" );
		assertThat( claims.get( "role" ) ).isEqualTo( "ADMIN" );
		// System.out.println( claims.get( "privs" ) );
		assertThat( ( (String)claims.get( "privs" ) ).indexOf( "all users" ) ).isGreaterThan( -1 );
	}
	
	@Test
	void testInvalidLoginUsername() {
		// Given: invalid login username
		String body = "{ \"username\": \"vetrootbeer\", \"password\": \"root\" }";
		String uri = getBaseUriForUsers() + "/login";
		
		// When: the user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		
		// Then: we return a NOT FOUND status (404)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
	}
	
	@Test
	void testInvalidLoginPassword() {
		// Given: invalid login password
		String body = "{ \"username\": \"vetroot\", \"password\": \"rootbeer\" }";
		String uri = getBaseUriForUsers() + "/login";
		
		// When: the user logs in
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
		
		// Then: we return an UNAUTHORIZED status (401)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.UNAUTHORIZED );
	}
	
	// NOTE: SpringBoot seems to have a real problem with bodies in GET requests. Specifically, it will set the body to null
	//       before passing it off to the controller, and if the controller is expecting a body, it becomes null and you wind up
	//       with "Required REST body is missing" errors.
	
	// The following helps with this problem, 
	//   https://mekaso.rocks/get-requests-with-a-request-body-spring-resttemplate-vs-apache-httpclient
	//   but maybe instead, when logging in, there is a way to send back the jwt as an Authorization header in the response,
	//   and subsequent requests may be able to send that token in a header.
	//   In Postman, will need to set Authorization > Bearer Token to the relevant JWT.
	
	@Test
	void testObtainUsersFromAdmin() {
		// Given: admin credentials
		// When: that admin logs in and later requests all users

		// Then: we return OK (200) with a list of users
		// assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		
		// The following sequence is confirmed to work, and also works in Postman.
		String jwt = obtainJwtFromValidLogin( "vetroot", "root" );
		String uri = getBaseUriForUsers();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( MediaType.APPLICATION_JSON );
		// headers.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );
	    headers.add( "User-Agent", "Spring's RestTemplate" );  // value can be whatever
	    headers.add( "Authorization", "Bearer " + jwt );		
		ResponseEntity<List<User>> response2 = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		System.out.println( response2 );
		List<User> users = response2.getBody();
		System.out.println( users );
		
	}

}



