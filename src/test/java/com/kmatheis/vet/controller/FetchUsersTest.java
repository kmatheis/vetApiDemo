package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import com.kmatheis.vet.entity.LoginRequest;
import com.kmatheis.vet.entity.ServerKey;
import com.kmatheis.vet.entity.User;
import com.kmatheis.vet.service.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class FetchUsersTest extends FetchUsersTestSupport {

	@Nested
	@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
	@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
	@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
	    "classpath:Vet_Api_Demo_Schema.sql",  
	    "classpath:Vet_Api_Demo_Data.sql" }, 
	    config = @SqlConfig( encoding = "utf-8" ) )
	class non500Tests extends FetchUsersTestSupport {
		
//		@Test
//		void testValidSuperuserLogin() {  // Deprecated but here for reference
//			// Given: valid login credentials
//			String body = "{ \"username\": \"vetroot\", \"password\": \"root\" }";
//			String uri = getBaseUriForUsers() + "/login";
//			
//			// When: the user logs in
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType( MediaType.APPLICATION_JSON );
//			HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
//			ResponseEntity<UserReply> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, UserReply.class );
//			
//			// Then: we return an OK status along with a JWT
//			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
//			UserReply actual = response.getBody();
//			assertThat( actual.getUser().getUsername() ).isEqualTo( "vetroot" );
//			assertThat( actual.getUser().getRoleId() ).isEqualTo( 1 );
//			String[] parts = actual.getJwt().split( "\\." );  // since input is interpreted as a regex
//			assertThat( parts ).hasSize( 3 );		         
//						         
//			Claims claims = Jwts.parser().setSigningKey( ServerKey.workingKey ).parseClaimsJws( actual.getJwt() ).getBody();
//			assertThat( claims.getSubject() ).isEqualTo( "VET API DEMO" );
//			assertThat( claims.get( "role" ) ).isEqualTo( "ADMIN" );
//			// System.out.println( claims.get( "privs" ) );
//			assertThat( ( (String)claims.get( "privs" ) ).indexOf( "all users" ) ).isGreaterThan( -1 );
//		}
		
		@Test
		void testValidSuperuserLoginWithAuthHeader() {
			// Given: valid login credentials
			String body = "{ \"username\": \"vetroot\", \"password\": \"root\" }";
			String uri = getBaseUriForUsers() + "/login";
			
			// When: the user logs in
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType( MediaType.APPLICATION_JSON );
			HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
			ResponseEntity<User> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, User.class );
			
			// Then: we return an OK status along with a JWT
			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
			User actual = response.getBody();
			assertThat( actual.getUsername() ).isEqualTo( "vetroot" );
			assertThat( actual.getRoleId() ).isEqualTo( 1 );
			String jwt = response.getHeaders().get( "Authorization" ).get( 0 ); 
			String[] parts = jwt.split( "\\." );  // since input is interpreted as a regex
			assertThat( parts ).hasSize( 3 );		         
						         
			Claims claims = Jwts.parser().setSigningKey( ServerKey.workingKey ).parseClaimsJws( jwt ).getBody();
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
			ResponseEntity<User> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, User.class );
			
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
			ResponseEntity<User> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, User.class );
			
			// Then: we return an UNAUTHORIZED status (401)
			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.UNAUTHORIZED );
		}
		
		// NOTE: SpringBoot seems to have a real problem with bodies in GET requests. Specifically, it will set the body to null
		//       before passing it off to the controller, and if the controller is expecting a body, it becomes null and you wind up
		//       with "Required REST body is missing" errors.
		
		//   If we wanted to implement a GraphQL API, the following helps with this problem: 
		//   https://mekaso.rocks/get-requests-with-a-request-body-spring-resttemplate-vs-apache-httpclient
		//   However, this is a REST API.
		//   So, instead, when logging in, there is a way to send back the jwt as an Authorization header in the response,
		//   and subsequent requests may be able to send that token in a header.
		//   When testing in Postman, one will need to set Authorization > Bearer Token to the relevant JWT that is returned in the Authorization header.
		
		@Test
		void testObtainUsersFromAdmin() {
			// Given: admin credentials
			// When: that admin logs in and later requests all users
		    HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
			String uri = getBaseUriForUsers();
			ResponseEntity<List<User>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
			
			// Then: we return OK (200) with a list of users
			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
			List<User> users = response.getBody();
			assertThat( users ).hasSizeGreaterThanOrEqualTo( 1 );
			assertThat( users.get( 0 ).getUsername().equals( "vetroot" ) );
		}
		
		// TODO: Need more finesse to handle stored procedures vis-a-vis an H2 DB. 
		//   For now, the /someusers call currently employs simple SQL instead of a stored procedure.
		@Test
		void testObtainSomeUsersFromAdmin() {
			// Given: admin credentials
			// When: that admin logs in and later requests users with "root" in their name
		    HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
			String uri = String.format( "%s?namecontains=%s", getBaseUriForSomeUsers(), "root" );
			ResponseEntity<List<User>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
			
			// Then: we return OK (200) with a list of users
			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
			List<User> users = response.getBody();
			assertThat( users ).hasSizeGreaterThanOrEqualTo( 1 );
			assertThat( users.get( 0 ).getUsername().equals( "vetroot" ) );
		}
		
	}
	
	// TODO: Mockito fails here. Need to fix this.
	/*
	@Nested
	@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
	@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
	@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
	    "classpath:Vet_Api_Demo_Schema.sql",  
	    "classpath:Vet_Api_Demo_Data.sql" }, 
	    config = @SqlConfig( encoding = "utf-8" ) )
	class just500Tests {
		@MockBean
		private AuthService authService;
		
		@Test
		void testThatUnplannedErrorGives500() throws AuthenticationException {
			// Given: valid login credentials and a server gremlin
			String body = "{ \"username\": \"vetroot\", \"password\": \"root\" }";
			String uri = getBaseUriForUsers() + "/login";
			LoginRequest newLR = LoginRequest.builder().username( "vetroot" ).password( "root" ).build();
			doThrow( new RuntimeException( "Server fall down go boom" ) ).when( authService ).login( newLR );
			
			// When: the user logs in
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType( MediaType.APPLICATION_JSON );
			HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
			ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( 
				uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} 
			);
			
			// Then: an internal server error (500) status code is returned
			assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.INTERNAL_SERVER_ERROR );  // i.e., 500
			
			// And: an error message is returned
			Map<String, Object> error = response.getBody();  // body has a Map<String, Object> now
			
			assertErrorMessageValid( error, HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
	}
	*/
	
}



