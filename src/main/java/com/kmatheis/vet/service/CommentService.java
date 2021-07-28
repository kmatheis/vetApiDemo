package com.kmatheis.vet.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmatheis.vet.dao.AnimalDao;
import com.kmatheis.vet.dao.CommentDao;
import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Comment;
import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private AnimalDao animalDao;
	
	// A little unusual to return the whole animal, but it's important to look at all the comments with regard to one animal.
	public Animal addCommentToAid( String jwt, Long aid, Comment comment ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "add comments", "read animals" ) );
		authService.authorize( jwt, neededPrivs );
		
		// log.debug( "in CommentService.addCommentToAid, comment ondate is {}", comment.getOndate() );
		Animal a = animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
		// log.debug( "...and animal is {}", a );
		
		// Comment will be scrubbed to *only* have ondate, type, and comment prior to insertion
		comment.setPk( null );
		comment.setId( null );
		comment.setAnimalId( null );
		if ( comment.getOndate() == null ) {
			Timestamp t = new Timestamp( Instant.now().toEpochMilli() );
			comment.setOndate( t );
		}
		if ( comment.getType() == null || comment.getComment() == null || comment.getComment().length() < 3 ) {
			throw new IllegalAttemptException( "Comment must have a type and at least three characters of comment text." );
		}
		
		return commentDao.addCommentToAnimal( comment, a.getPk(), a.getId() );
	}

	private Animal verifyAnimal( Long aid, Long cid ) {
		Animal foundAnimal = animalDao.fetchAnimalById( aid ).orElseThrow( () -> new NoSuchElementException( "Animal with id " + aid + " does not exist." ) );
		boolean found = foundAnimal.getComments().stream().anyMatch( (c) -> c.getId().equals( cid ) );
		
		if ( !found ) {
			throw new NoSuchElementException( "Comment with id " + cid + " does not belong to animal with id " + aid + "." );
		}
		
		return foundAnimal;
	}
	
	public Animal deleteComment( String jwt, Long aid, Long cid ) throws AuthenticationException {
		List<String> neededPrivs = new ArrayList<String>( Arrays.asList( "del comments" ) );
		authService.authorize( jwt, neededPrivs );
		
		Animal a = verifyAnimal( aid, cid );
		return commentDao.deleteComment( aid, cid );
	}

}
