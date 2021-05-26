package com.kmatheis.vet.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
	private Long id;
	private String username;
	private String hash;
	private Long roleId;
	// Instead of using JWTs, could have Java itself maintain the session, but thought to have some fun with JWTs.
//	private String rolename;    
//	private List<String> privs;
//	private Date expire;        
}

