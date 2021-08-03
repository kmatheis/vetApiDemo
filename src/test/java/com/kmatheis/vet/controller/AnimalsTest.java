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

import com.kmatheis.vet.Constants;
import com.kmatheis.vet.controller.support.BaseTest;
import com.kmatheis.vet.entity.Animal;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class AnimalsTest extends BaseTest {

	@Test
	void testGetAnimals() {
		// Given: rec credentials
		// When: that rec logs in and searches for animals in pid 1001
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/animals" );

		ResponseEntity<List<Animal>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		// Then: We see all three animals.
		List<Animal> animals = response.getBody();
		assertThat( animals.size() ).isEqualTo( 3 );
	}
	
	@Test
	void testGetAnimal() {
		// Given: rec credentials
		// When: that rec logs in and searches for animal id 10001
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001" );
		
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), Animal.class );
		// Then: that animal is found.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		Animal a = response.getBody();
		assertThat( a.getName() ).isEqualTo( "Sunshine" );
		assertThat( a.getComments() ).hasSize( 3 );
	}
	
	@Test
	void testAddAnimal() {
		// Given: rec credentials
		// When: that rec logs in and adds an animal
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/animals" );
		String body = "{ \"name\": \"Luna\", \"species\": \"SUGAR_GLIDER\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add is successful.
		Animal a = response.getBody();
		assertThat( a.getName() ).isEqualTo( "Luna" );
		assertThat( a.getProfileId() ).isEqualTo( 1001 );
	}
	
	@Test
	void testAddUnsupportedSpeciedAnimal() {
		// Given: rec credentials
		// When: that rec logs in and adds an animal of an unsupported species
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/animals" );
		String body = "{ \"name\": \"Blanca\", \"species\": \"WHITESPIKE\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails with a 400.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
	}
	
	@Test
	void testDeleteAnimal() {
		// Given: tech credentials
		// When: that tech logs in and deletes an animal
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/animals/10004" );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), String.class );
		// Then: the delete succeeds.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully deleted animal 10004" );
	}
	
	@Test
	void testModifyAnimal() {
		// Given: rec credentials
		// When: that rec logs in and moves an animal from 1002 to 1001
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/animals/10004" );
		String body = "{ \"name\": \"Torty\", \"profileId\": 1001 }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, String.class );
		// Then: the modification succeeds.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully modified animal." );
	}
	
	@Test
	void testModifyAnimalKeepingName() {
		// Given: rec credentials
		// When: that rec logs in and moves an animal from 1002 to 1001
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1002/animals/10004" );
		String body = "{ \"profileId\": 1001 }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.PUT, bodyEntity, String.class );
		// Then: the modification succeeds.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully modified animal." );
	}
	
	@Test
	void testAddAnimalNameTooShort() {
		// Given: rec credentials
		// When: that rec logs in and adds an animal of an unsupported species
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/animals" );
		String body = "{ \"name\": \"Bl\", \"species\": \"SUGAR_GLIDER\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails with a 400.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		Map<String, Object> error = response.getBody();
		assertThat( error.get( "message" ) ).isEqualTo( "name should have at least " + Constants.ANIMALNAME_MIN_LENGTH + " characters." );
	}
	
	@Test
	void testAddAnimalNameIsNull() {
		// Given: rec credentials
		// When: that rec logs in and adds an animal of an unsupported species
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "profiles/1001/animals" );
		String body = "{ \"species\": \"SUGAR_GLIDER\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the add fails with a 400.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		Map<String, Object> error = response.getBody();
		assertThat( ((String) error.get( "message" ) ).indexOf( "Animal's name must exist." ) ).isGreaterThanOrEqualTo( 0 );
	}

}
