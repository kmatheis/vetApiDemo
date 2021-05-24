package com.kmatheis.vet.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReply {
	private User user;
	private String jwt;
}
