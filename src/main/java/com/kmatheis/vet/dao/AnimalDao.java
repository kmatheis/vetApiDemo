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

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.entity.Species;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnimalDao {
	
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	public List<Animal> fetchAnimalsByProfile( Profile p ) {
		String sql = "select * from animals where profile_fk = :profile_fk";
		Map<String, Object> params = new HashMap<>();
		params.put( "profile_fk", p.getPk() );
		
		return npJdbcTemplate.query( sql, params, 
				new RowMapper<Animal>() {
					@Override
					public Animal mapRow( ResultSet rs, int rowNum ) throws SQLException {
						return Animal.builder()
								.pk( rs.getLong( "pk" ) )
								.id( rs.getLong( "id" ) )
								.name( rs.getString( "name" ) )
								.species( Species.valueOf( rs.getString( "species" ) ) )  // DB enum to String to Java Enum. valueOf will only recognize if the string is all caps.
								.profile( p )
								.build();
					}
				}
		);
	}
}
