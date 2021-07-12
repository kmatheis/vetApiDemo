package com.kmatheis.vet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.entity.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OwnerDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	public List<Owner> fetchOwnersByProfile( Profile p ) {
		String sql = "select * from owners where profile_fk = :profile_fk";
		Map<String, Object> params = new HashMap<>();
		params.put( "profile_fk", p.getPk() );
		
		return npJdbcTemplate.query( sql, params, 
				new RowMapper<Owner>() {
					@Override
					public Owner mapRow( ResultSet rs, int rowNum ) throws SQLException {
						return Owner.builder()
								.pk( rs.getLong( "pk" ) )
								.id( rs.getLong( "id" ) )
								.name( rs.getString( "name" ) )
								.phone( rs.getString( "phone" ) )
								.profileId( p.getId() )
								.build();
					}
				}
		);
	}
}
