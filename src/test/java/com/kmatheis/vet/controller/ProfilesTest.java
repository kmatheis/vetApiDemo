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
import com.kmatheis.vet.entity.Profile;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class ProfilesTest extends BaseTest {

	@Test
	void testObtainProfiles() {
		// Given: rec credentials
		// When: that rec logs in and searches for all profiles
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles" );

		ResponseEntity<List<Profile>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		// Then: We see both profiles.
		List<Profile> profiles = response.getBody();
		// System.out.println( profiles );
		assertThat( profiles.size() ).isEqualTo( 2 );
	}
	
	@Test
	void testModifyProfile() {
		// Given: rec credentials
		// When: that rec attempts to modify a profile's name (can only modify profile name using this endpoint)
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002" );
		String body = "{ \"name\": \"Jannaa\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, String.class );
		// Then: We see the profile modified.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully modified profile." );
		
		uri = String.format( "%s/%s", getBaseUri(), "profiles/1002" );
		ResponseEntity<Profile> response2 = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), Profile.class );
		assertThat( response2.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response2.getBody().getName() ).isEqualTo( "Jannaa" );
	}
	
	@Test
	void testModifyProfileWithBadCharacter() {
		// Given: rec credentials
		// When: that rec attempts to modify a profile's name with a bad character
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002" );
		String body = "{ \"name\": \"Ja[nn]aa\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: The change fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		Map<String, Object> error = response.getBody();
		assertThat( error.get( "message" ) ).isEqualTo( "name should be just letters, numbers, underscores, spaces, and/or certain special characters." );
	}
	
	@Test
	void testModifyProfileByProvidingNoName() {
		// Given: rec credentials
		// When: that rec attempts to modify a profile's name with a bad character
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002" );
		String body = "{ }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: The change fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		Map<String, Object> error = response.getBody();
		assertThat( error.get( "message" ) ).isEqualTo( "name must exist." );
	}

}
