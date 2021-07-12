package com.kmatheis.vet.internal;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class SqlParams {
	public String sql;
	public MapSqlParameterSource source = new MapSqlParameterSource();
}
