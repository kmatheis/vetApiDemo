package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.kmatheis.vet.dao.UserDao;
import com.kmatheis.vet.entity.Role;
import com.kmatheis.vet.entity.User;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class ModifyUsersTest extends ModifyUsersTestSupport {

	// TODO: Rewrite test to not do this
	@Autowired
	UserDao userDao;

	@Test
	void testAttemptToDeleteSuperuser() {
		// Given: admin credentials
		// When: that admin logs in and attempts to delete the superuser
	    HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s/1", getBaseUriForUsers() );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		
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
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		
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
	void testAddBadRole() {
		// Given: admin credentials
		// When: that admin logs in and adds a bad role (i.e., it has special characters)
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"atech\", \"password\": \"atech\", \"rolename\": \"TECHN!C!AN\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		Map<String, Object> error = response.getBody();
		assertThat( error.get( "message" ) ).isEqualTo( "rolename should be just letters, numbers, and/or underscores." );
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
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		
		// Then: we return UNAUTHORIZED (401)
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.UNAUTHORIZED );
	}
	
	@Test
	void testModifyingUser() {
		// Given: admin credentials
		// When: that admin logs in, creates a user...
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s", getBaseUriForUsers() );
		String body = "{ \"username\": \"anadmin\", \"password\": \"anadmin\", \"rolename\": \"ADMIN\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		// ...and modifies that user
		Optional<User> oUser = userDao.fetchUser( "anadmin" );
		assertThat( oUser.isPresent() ).isEqualTo( true );
		User user = oUser.get();
		uri = String.format( "%s/%s", getBaseUriForUsers(), user.getId() );
		body = "{ \"username\": \"arec\", \"password\": \"arec\", \"rolename\": \"RECEPTIONIST\" }";
		bodyEntity = new HttpEntity<>( body, headers );
		response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, String.class );
		
		// Then: we return OK with the user successfully modified
		assertThat( response.getBody() ).isEqualTo( "Successfully modified user." );
		oUser = userDao.fetchUser( "arec" );
		assertThat( oUser.isPresent() ).isEqualTo( true );
		user = oUser.get();
		Optional<Role> oRole = userDao.fetchRoleByName( "RECEPTIONIST" );
		assertThat( oRole.isPresent() ).isEqualTo( true );
		Role role = oRole.get();
		assertThat( user.getRoleId() ).isEqualTo( role.getId() );
	}

}
