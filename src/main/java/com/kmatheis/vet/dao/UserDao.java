package com.kmatheis.vet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.kmatheis.vet.entity.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserDao {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public Optional<User> fetchUser( String username ) {
		String sql = "select * from users where username = :username";
		Map<String, Object> params = new HashMap<>();
		params.put( "username", username );
		return Optional.ofNullable( jdbcTemplate.query( sql,  params, new UserResultSetExtractor() ) );
	}
	
	class UserResultSetExtractor implements ResultSetExtractor<User> {
		@Override
		public User extractData( ResultSet rs ) throws SQLException, DataAccessException {
			rs.next();
			return User.builder()
					.id( rs.getLong( "id" ) )
					.username( rs.getString( "username" ) )
					.hash( rs.getString( "hash" ) )
					.role_id( rs.getLong( "role_id" ) )
					.build();
		}
	}
}
