package com.kmatheis.vet.controller;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Comment;

public interface CommentController {
	
	// Not really going to get an individual comment since they're always looked at in the context of an animal
	
	@PostMapping( "/animals/{aid}/comments" )
	@ResponseStatus( code = HttpStatus.OK )
	public Animal addCommentToAid(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long aid,
			@RequestBody Comment comment
	) throws AuthenticationException;
	
	@DeleteMapping( "/animals/{aid}/comments/{cid}" )
	@ResponseStatus( code = HttpStatus.OK )
	public Animal deleteComment(
			@RequestHeader( "Authorization" ) String bearerJwt, 
			@PathVariable Long aid,
			@PathVariable Long cid
	) throws AuthenticationException;

}
