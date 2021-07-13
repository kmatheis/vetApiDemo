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

import com.kmatheis.vet.controller.support.BaseTest;
import com.kmatheis.vet.dto.Reservation;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles( "test" )  // looks for application-test.yaml in src/test/resources
@Sql( scripts = {  // execute these in order before each test (using application-test.yaml, so, the H2 in-memory db)
    "classpath:Vet_Api_Demo_Schema.sql",  
    "classpath:Vet_Api_Demo_Data.sql" }, 
    config = @SqlConfig( encoding = "utf-8" ) )
public class ReservationsTest extends BaseTest {

	@Test
	void testFetchReservations() {
		// Given: rec credentials
		// When: that rec logs in and searches for reservations for animal 10004
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "reservations/animals/10004" );

		ResponseEntity<List<Reservation>> response = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		// Then: We see both reservations.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		List<Reservation> reservations = response.getBody();
		// System.out.println( reservations );
		assertThat( reservations.size() ).isEqualTo( 2 );
	}
	
	@Test
	void testAddLegitimateReservation() {
		// Given: rec credentials
		// When: that rec logs in and adds a res for animal 10004 for room id 106 from 2021-03-20 to 2021-04-01
		HttpHeaders headers = obtainHeadersFromValidLogin( "vetrec", "vetrec" );
		String uri = String.format( "%s/%s", getBaseUri(), "reservations" );
		String body = "{ \"aid\": 10004, \"rid\": 106, \"fromdate\": \"2021-03-20\", \"todate\": \"2021-04-01\" }";
		headers.setContentType( MediaType.APPLICATION_JSON );
		HttpEntity<String> bodyEntity = new HttpEntity<>( body, headers );
		ResponseEntity<String> response = getRestTemplate().exchange( uri, HttpMethod.POST, bodyEntity, String.class );
		// Then: We see the reservation.
		assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
		assertThat( response.getBody() ).isEqualTo( "Successfully added reservation." );
		uri = String.format( "%s/%s", getBaseUri(), "reservations/animals/10004" );
		ResponseEntity<List<Reservation>> response2 = getRestTemplate().exchange( uri, HttpMethod.GET, new HttpEntity<>( "parameters", headers ), new ParameterizedTypeReference<>() {} );
		assertThat( response2.getStatusCode() ).isEqualTo( HttpStatus.OK );
		List<Reservation> reservations = response2.getBody();
		assertThat( reservations.size() ).isEqualTo( 3 );
	}
}
