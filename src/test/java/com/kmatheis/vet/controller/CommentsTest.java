package com.kmatheis.vet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
			arguments( "10001", "2021-06-02 13:51:00", "VAX", "FVRCP", 4 ),
			arguments( "10002", "2021-06-02 13:55:00", "OTHER", "Brief lump under right leg, but tested benign", 4 ),
			arguments( "10003", "2021-06-04 08:41:00", "CHECKUP", "Iana looks healthy!", 1 )
		);
	}
	
	@ParameterizedTest
	@MethodSource( "com.kmatheis.vet.controller.CommentsTest#paramsForValidComments" )
	void testAddComment( String aid, String ondate, String type, String comment, Integer correctCount ) {
		// Given: tech credentials
		// When: that tech logs in and adds a comment
		HttpHeaders headers = obtainHeadersFromValidLogin( "vettech", "vettech" );
		
		String uri = String.format( "%s/%s", getBaseUri(), "animals/" + aid + "/comments" );
		String body = "{ \"ondate\": \"" + ondate + "\", \"type\": \"" + type + "\", \"comment\": \"" + comment + "\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<Animal> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, Animal.class );
		// Then: the add is successful.
		Animal a = response.getBody();
		System.out.println( a );
		assertThat( a.getId() ).isEqualTo( Integer.parseInt( aid ) );
		assertThat( a.getComments() ).hasSize( correctCount );
	}

}
