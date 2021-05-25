package com.kmatheis.vet.entity;

import java.security.Key;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerKey {
	
	private Long id;
	private String serverKey;
	public static Key workingKey = null;
	
}
