package com.kmatheis.vet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.kmatheis.vet.entity.Role;
import com.kmatheis.vet.entity.ServerKey;
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
		return jdbcTemplate.query( sql, params, new UserResultSetExtractor() );
	}
	
	class UserResultSetExtractor implements ResultSetExtractor<Optional<User>> {
		@Override
		public Optional<User> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			if ( rs.next() ) {  
				return Optional.of( User.builder()
						.id( rs.getLong( "id" ) )
						.username( rs.getString( "username" ) )
						.hash( rs.getString( "hash" ) )
						.roleId( rs.getLong( "role_id" ) )
						.build()
				);
			}
			return Optional.empty();
		}
	}
	
	public List<ServerKey> fetchServerKeys() {
		String sql = "select * from server_keys limit 1";
		
		return jdbcTemplate.query( sql,   // don't need params for this one
			new RowMapper<>() {
				@Override
				public ServerKey mapRow( ResultSet rs, int rowNum ) throws SQLException {
					return ServerKey.builder()
							.id( rs.getLong( "id" ) )
							.serverKey( rs.getString( "server_key" ) )
							.build();
				}
			}
		);
	}
	
	public Role fetchRole( long id ) {
		String sql = "select * from roles where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		
		List<Role> roles = jdbcTemplate.query( sql, params, new RowMapper<Role>() {
				@Override
				public Role mapRow( ResultSet rs, int rowNum ) throws SQLException {
					return Role.builder()
							.id( rs.getLong( "id" ) )
							.rolename( rs.getString( "rolename" ) )
							.build();
				}
			}
		);
		
		return roles.get( 0 );
	}
	
	public List<String> fetchPrivs( long roleId ) {
		String sql = "select * from privs where role_id = :role_id";
		Map<String, Object> params = new HashMap<>();
		params.put( "role_id", roleId );
		return jdbcTemplate.query( sql, params, 
			new RowMapper<String>() {
				@Override
				public String mapRow( ResultSet rs, int rowNum ) throws SQLException {
					return rs.getString( "description" );
				}
			}
		);
	}
	
	public List<User> fetchUsers() {
		String sql = "select * from users";
		return jdbcTemplate.query( sql, 
				new RowMapper<User>() {
					@Override
					public User mapRow( ResultSet rs, int rowNum ) throws SQLException {
						return User.builder()
								.id( rs.getLong( "id" ) )
								.username( rs.getString( "username" ) )
								.hash( rs.getString( "hash" ) )
								.roleId( rs.getLong( "role_id" ) )
								.build();
					}
				}
		);
		
	}
}
