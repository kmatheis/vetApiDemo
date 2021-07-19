package com.kmatheis.vet.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.AnimalDao;
import com.kmatheis.vet.dao.ReservationDao;
import com.kmatheis.vet.dao.RoomDao;
import com.kmatheis.vet.dto.Reservation;
import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Room;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationService {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private AnimalDao animalDao;
	
	@Autowired
	private RoomDao roomDao;
	
	@Autowired
	private ReservationDao reservationDao;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat( "yyyy-MM-dd" );
	
	public List<Reservation> getReservationsByAid( String jwt, Long aid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "read reservations" ) );
		authService.authorize( jwt, neededPrivs );
		Animal a = animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
		return reservationDao.fetchReservationsByApk( a.getPk() );
	}

	public String addReservation( String jwt, Reservation res ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add reservations" ) );
		authService.authorize( jwt, neededPrivs );
		Long aid = res.getAid();
		Long rid = res.getRid();
		Date fromdate = res.getFromdate();
		Date todate = res.getTodate();
		// log.debug( "in ReservationService.addReservation, fromdate={} and todate={} (might be off by one due to bad DB/sys sync)", fromdate, todate );
		String fdstr = dateFormatter.format( fromdate );
		String tdstr = dateFormatter.format( todate );
		// log.debug( "in ReservationService.addReservation, fdstr={} and tdstr={}", fdstr, tdstr );
		if ( fromdate.compareTo( todate ) >= 0 ) {
			throw new IllegalAttemptException( "Provided fromdate must be strictly earlier than provided todate." );
		}
		Animal a = animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
		Room r = roomDao.fetchRoomById( rid ).orElseThrow( () -> new NoSuchElementException( "Room with id " + rid + " does not exist." ) );
		// perhaps some date checking is in order
		if ( r.getMaxcap() == 1 && !reservationDao.isRoomClear( rid, fdstr, tdstr ) ) {
			throw new IllegalAttemptException( "Room id " + rid + " is already booked for at least one day in [" + fdstr + "; " + tdstr + "). "
					+ "(If you're experiencing an off-by-one error, dates have not yet been synchronized between system, MySQL, and H2.)" );
		}
		// TODO: Similarly, check if animal is booked elsewhere in that date range.
		// TODO: Also check maxcap of room. (Need more finesse with isRoomClear to accommodate/check maxcap.)
		return reservationDao.addReservation( a.getPk(), r.getPk(), fdstr, tdstr );
	}

}
