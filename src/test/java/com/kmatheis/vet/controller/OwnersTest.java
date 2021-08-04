package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

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

import com.kmatheis.vet.controller.support.BaseTest;
import com.kmatheis.vet.entity.Owner;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class OwnersTest extends BaseTest {

	@Test
	void testGetOwners() {
		// Given: rec credentials
		// When: that rec logs in and searches for owners in pid 1001
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/owners" );

		ResponseEntity<List<Owner>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		// Then: We see both owners.
		List<Owner> owners = response.getBody();
		assertThat( owners.size() ).isEqualTo( 2 );
	}
	
	@Test
	void testAddOwner() {
		// Given: rec credentials
		// When: that rec logs in and adds an owner
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners" );
		String body = "{ \"name\": \"Rami Rami\", \"phone\": \"800-555-0109\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Owner> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Owner.class );
		// Then: the add is successful.
		Owner o = response.getBody();
		assertThat( o.getName() ).isEqualTo( "Rami Rami" );
		assertThat( o.getProfileId() ).isEqualTo( 1002 );
		assertThat( o.getId() ).isEqualTo( 5005 );
	}
	
	@Test
	void testDeleteOwner() {
		// Given: admin credentials
		// When: that admin logs in and deletes an owner
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetroot", "root" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners/5004" );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), String.class );
		// Then: the delete succeeds.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully deleted owner 5004" );
	}
	
	@Test
	void testModifyOwner() {
		// Given: rec credentials
		// When: that rec logs in and changes an owner's name and phone number
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners/5004" );
		String body = "{ \"name\": \"Jenny Tutone\", \"phone\": \"800-867-5309\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, String.class );
		// Then: the modification succeeds.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully modified owner." );
	}
	
	@Test
	void testAddOwnerWithNoName() {
		// Given: rec credentials
		// When: that rec logs in and adds an owner with no name
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners" );
		String body = "{ \"phone\": \"800-555-0109\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( ( (String) response.getBody().get( "message" ) ).indexOf( "Owner's name must exist." ) ).isGreaterThanOrEqualTo( 0 );
	}

	@Test
	void testAddOwnerWithNameTooShort() {
		// Given: rec credentials
		// When: that rec logs in and adds an owner with no name
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners" );
		String body = "{ \"name\": \"Ra\", \"phone\": \"800-555-0109\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "name should have at least 3 characters." );
	}
	
	@Test
	void testAddOwnerWithNameWithBadCharacters() {
		// Given: rec credentials
		// When: that rec logs in and adds an owner with no name
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners" );
		String body = "{ \"name\": \"Ra \", \"phone\": \"800-555-0109\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "name should be just letters with possibly spaces in between." );
	}
	
	@Test
	void testModifyOwnerWithNameTooShort() {
		// Given: rec credentials
		// When: that rec logs in and changes an owner's name and phone number
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/owners/5004" );
		String body = "{ \"name\": \"JT\", \"phone\": \"800-867-5309\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the modification fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "name should have at least 3 characters." );
	}
}
