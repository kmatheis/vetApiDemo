package com.kmatheis.vet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class UserDescription {
	String username;
	String password;
	String rolename;
}
