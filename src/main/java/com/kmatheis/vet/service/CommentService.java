package com.kmatheis.vet.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.AnimalDao;
import com.kmatheis.vet.dao.CommentDao;
import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Comment;
import com.kmatheis.vet.exception.IllegalAttemptException;

// import lombok.extern.slf4j.Slf4j;

@Service
// @Slf4j
public class CommentService {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private AnimalDao animalDao;
	
	private Animal verifyAnimal( Long aid ) {
		return animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
	}
	
	// A little unusual to return the whole animal, but it's important to look at all the comments with regard to one animal.
	public Animal addCommentToAid( String jwt, Long aid, Comment comment ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add comments", "read animals" ) );
		authService.authorize( jwt, neededPrivs );
		
		// log.debug( "in CommentService.addCommentToAid, comment ondate is {}", comment.getOndate() );
		Animal a = verifyAnimal( aid );
		// log.debug( "...and animal is {}", a );
		
		// Comment will be scrubbed to *only* have ondate, type, and comment prior to insertion
		comment.setPk( null );
		comment.setId( null );
		comment.setAnimalId( null );
		
		Timestamp now = new Timestamp( Instant.now().toEpochMilli() );
		
		if ( comment.getOndate() == null ) {
			Timestamp t = now;
			comment.setOndate( t );
		}
		if ( comment.getOndate().after( now ) ) {
			throw new IllegalAttemptException( "Cannot have an ondate which is in the future." );
		}
		
		return commentDao.addCommentToAnimal( comment, a.getPk(), a.getId() );
	}

	private Animal verifyAnimalComment( Long aid, Long cid ) {
		Animal foundAnimal = verifyAnimal( aid );
		boolean found = foundAnimal.getComments().stream().anyMatch( (c) -> c.getId().equals( cid ) );
		if ( !found ) {
			throw new NoSuchElementException( "Comment with id " + cid + " does not belong to animal with id " + aid + "." );
		}
		return foundAnimal;
	}
	
	public Animal deleteComment( String jwt, Long aid, Long cid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "del comments" ) );
		authService.authorize( jwt, neededPrivs );
		
		verifyAnimalComment( aid, cid );
		return commentDao.deleteComment( aid, cid );
	}

}
