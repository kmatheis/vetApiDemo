package com.kmatheis.vet.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Comment;
import com.kmatheis.vet.service.AuthService;
import com.kmatheis.vet.service.CommentService;

@RestController
public class BasicCommentController implements CommentController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private CommentService commentService;
	
	@Override
	public Animal addCommentToAid( String bearerJwt, Long aid, Comment comment ) throws AuthenticationException {
		return commentService.addCommentToAid( authService.bearerToJwt( bearerJwt ), aid, comment );
	}

}
