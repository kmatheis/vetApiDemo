package com.kmatheis.vet.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {
	private Long id;
	private String rolename;
}
