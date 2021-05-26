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
}

