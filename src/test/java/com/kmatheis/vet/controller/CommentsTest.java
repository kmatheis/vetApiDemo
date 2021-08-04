package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import com.kmatheis.vet.entity.Owner;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
class CommentsTest extends BaseTest {

	// aid, date, type, text, animal_fk, correct number of comments after insertion
	static Stream<Arguments> paramsForValidComments() {
		return Stream.of(
			arguments( "10001", "2021-06-01 01:01:01", "VAX", "FVRCP", 4 ),
			arguments( "10002", "2021-06-02 02:02:02", "OTHER", "Brief lump under right leg, but tested benign", 4 ),
			arguments( "10003", "2021-06-03 13:13:13", "CHECKUP", "Iana looks healthy!", 1 ),
			arguments( "10004", null, "FEEDING", "Munched on lettuce and carrots", 1 )
		);
	}
	
	@ParameterizedTest
	@MethodSource( "com.kmatheis.vet.controller.CommentsTest#paramsForValidComments" )
	void testAddComment( String aid, String ondate, String type, String comment, Integer correctCount ) {
		// Given: tech credentials
		// When: that tech logs in and adds a comment
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/" + aid + "/comments" );
		String ondateStr = ( ondate == null ? "" : "\"ondate\": \"" + ondate + "\", " );
		String body = "{ " + ondateStr + "\"type\": \"" + type + "\", \"comment\": \"" + comment + "\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add is successful.
		Animal a = response.getBody();
		// System.out.println( a );
		assertThat( a.getId() ).isEqualTo( Integer.parseInt( aid ) );
		assertThat( a.getComments() ).hasSize( correctCount );
	}

	@Test
	void testAddInvalidCommentBadAnimal() {
		// Given: tech credentials
		// When: that tech logs in and adds an invalid comment by way of bad animal id
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/19999/comments" );
		String body = "{ \"type\": \"VAX\", \"comment\": \"FPL\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add fails with a 404.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
	}
	
	@Test
	void testAddInvalidCommentBadType() {
		// Given: tech credentials
		// When: that tech logs in and adds an invalid comment by way of bad comment type
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"type\": \"GROOMING\", \"comment\": \"Hair was brushed out today.\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add fails with a 400.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
	}
	
	@Test
	void testAddInvalidCommentBadComment() {
		// Given: tech credentials
		// When: that tech logs in and adds an invalid comment by way of comment too short
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"type\": \"FOOD\", \"comment\": \"um\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add fails with a 400.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
	}
	
	@Test
	void testDeleteComment() {
		// Given: tech credentials
		// When: that tech logs in and deletes a comment
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments/50001" );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), Animal.class );
		// Then: the comment is deleted.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		Animal a = response.getBody();
		assertThat( a.getComments() ).hasSize( 2 );
	}
	
	@Test
	void testDeleteCommentBadId() {
		// Given: tech credentials
		// When: that tech logs in and deletes a comment whose id doesn't match the owning animal's id
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments/50002" );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.DELETE, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		// Then: the deletion fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
		assertThat( ((String) response.getBody().get( "message" )).indexOf( "Comment with id 50002 does not belong to animal with id 10001." ) ).isGreaterThanOrEqualTo( 0 );
	}
	
	@Test
	void testAddCommentWithNoComment() {
		// Given: tech credentials
		// When: that tech logs in and adds a comment whose comment field is missing
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"ondate\": \"2021-06-01 01:01:01\", \"type\": \"VAX\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the addition fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "comment must exist." );
	}
	
	@Test
	void testAddCommentWithNoType() {
		// Given: tech credentials
		// When: that tech logs in and adds a comment whose type field is missing
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"ondate\": \"2021-06-01 01:01:01\", \"comment\": \"Some vaccination\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the addition fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "type must exist." );
	}
	
	@Test
	void testAddCommentWithCommentTooShort() {
		// Given: tech credentials
		// When: that tech logs in and adds a comment whose comment field is too short
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"ondate\": \"2021-06-01 01:01:01\", \"comment\": \"So\", \"type\": \"VAX\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the addition fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( response.getBody().get( "message" ) ).isEqualTo( "comment should have at least " + Constants.COMMENT_MIN_LENGTH + " characters." );
	}
	
	@Test
	void testAddCommentWithOndateInFuture() {
		// Given: tech credentials
		// When: that tech logs in and adds a comment whose comment field is too short
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		String uri = String.format( "%s/%s", getBaseUri(), "animals/10001/comments" );
		String body = "{ \"ondate\": \"2525-06-01 01:01:01\", \"comment\": \"Some comment\", \"type\": \"OTHER\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Map<String, Object>> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, new ParameterizedTypeReference<>() {} );
		// Then: the addition fails.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
		assertThat( ((String) response.getBody().get( "message" )).indexOf( "Cannot have an ondate which is in the future." ) ).isGreaterThanOrEqualTo( 0 );
	}
}
