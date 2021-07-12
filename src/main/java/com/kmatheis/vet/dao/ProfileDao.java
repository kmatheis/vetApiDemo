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

import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.entity.Profile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProfileDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	@Autowired
	private OwnerDao ownerDao;
	
	@Autowired
	private AnimalDao animalDao;
	
	private Profile profileFromResultSet( ResultSet rs ) throws SQLException {
		// A Profile has Owners. Each Owner, in turn, has this Profile.
		// So, when we construct owners, we put the partially constructed Profile (just its pk) as input in the fetchOwnersByProfile call.
		// That method will find each Owner for this Profile, and importantly set each Owner's profile property to the passed-in profile.
		// This Profile will be completed later, but because we pre-allocated the space, we can get away with using the reference p.
		// (Similarly for Animals.)
		
		Long pk = rs.getLong( "pk" );
		Profile p = new Profile();
		p.setPk( pk );
		
		List<Owner> owners = ownerDao.fetchOwnersByProfile( p );
		List<Animal> animals = animalDao.fetchAnimalsByProfile( p );

		p.setId( rs.getLong( "id" ) );
		p.setName( rs.getString( "name" ) );
		p.setOwners( owners );
		p.setAnimals( animals );
		return p;
	}
	
	public List<Profile> fetchProfiles() {
		String sql = "select * from profiles";
		return npJdbcTemplate.query( sql, 
				new RowMapper<Profile>() {
					@Override
					public Profile mapRow( ResultSet rs, int rowNum ) throws SQLException {						
						return profileFromResultSet( rs );
					}
				}
		);
	}
	
	class ProfileResultSetExtractor implements ResultSetExtractor<Optional<Profile>> {
		@Override
		public Optional<Profile> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			if ( rs.next() ) {  
				return Optional.of( profileFromResultSet( rs ) );
			}
			return Optional.empty();
		}
	}
	
	public Optional<Profile> fetchProfileById( Long id ) {
		String sql = "select * from profiles where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		return npJdbcTemplate.query( sql, params, new ProfileResultSetExtractor() );
	}

}
