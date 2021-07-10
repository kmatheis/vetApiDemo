package com.kmatheis.vet.internal;

import com.kmatheis.vet.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReply {
	private User user;
	private String jwt;
}
