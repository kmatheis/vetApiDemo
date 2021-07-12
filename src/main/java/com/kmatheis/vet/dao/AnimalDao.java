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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.entity.Species;
import com.kmatheis.vet.exception.IllegalAttemptException;
import com.kmatheis.vet.internal.SqlParams;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnimalDao {
	
	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	private Long naid;
	
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
								.profileId( p.getId() )
								.build();
					}
				}
		);
	}

	public Animal addAnimalToProfile( String name, Species species, Profile p ) {
		// Illustrating obtaining the pk of the Animal upon insertion, and thus setting the pk attribute back to the returned Animal
		//   (even though we don't display it).
		SqlParams params = new SqlParams();
		params.sql = "insert into animals( name, species, id, profile_fk ) values ( :name, :species, :id, :profile_fk )";
		params.source.addValue( "name", name );
		params.source.addValue( "species", species.toString() );
		Long aid = getNextAnimalId();
		params.source.addValue( "id", aid );
		params.source.addValue( "profile_fk", p.getPk() );
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int status = npJdbcTemplate.update( params.sql, params.source, keyHolder );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to add animal." );
		}
		Long pk = keyHolder.getKey().longValue();
		return Animal.builder()
				.pk( pk )
				.id( aid )
				.name( name )
				.species( species )
				.profileId( p.getId() )
				.build();
	}
	
	// ==== Animal ID Management
	
	private Optional<Long> fetchLastAnimalId() {
		String sql = "select id from animals order by id desc limit 1";
		return npJdbcTemplate.query( sql, 
				new ResultSetExtractor<Optional<Long>>() {
					@Override
					public Optional<Long> extractData( ResultSet rs ) throws SQLException, DataAccessException {
						if ( rs.next() ) {  
							return Optional.of( rs.getLong( "id" ) );
						}
						return Optional.empty();
					}
				} 
		);
	}
	
	public synchronized Long getNextAnimalId() {
		if ( naid == null ) {
			Optional<Long> opid = fetchLastAnimalId();
			if ( opid.isPresent() ) {
				naid = opid.get() + 1;
			} else {
				naid = 10001L;
			}
		}
		Long out = naid;
		naid = naid + 1;
		return out;
	}
}
