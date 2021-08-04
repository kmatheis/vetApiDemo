package com.kmatheis.vet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import com.kmatheis.vet.exception.IllegalAttemptException;

// import lombok.extern.slf4j.Slf4j;

@Component
// @Slf4j
public class ProfileDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	@Autowired
	private OwnerDao ownerDao;
	
	@Autowired
	private AnimalDao animalDao;
	
	private BabyIdGenerator idGen;
	
	// ==== Profile ID management
	
	private Optional<Long> fetchLastProfileId() {
		String sql = "select id from profiles order by id desc limit 1";
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
	
	public synchronized Long getNextProfileId() {
		if ( idGen == null ) {
			idGen = new BabyIdGenerator( fetchLastProfileId().orElse( 1000L ) );
		}
		return idGen.getNextId();
	}
	
	// ==== Fetching profiles
	
	private Profile profileFromResultSet( ResultSet rs ) throws SQLException {
		// We can pass a partially constructed Profile to the fetchOwners and fetchAnimals calls
		//   as a convenience since it will contain both the pk and the id. Alternately, we could use inner joins.
		// Note that, since Profile.pk is @JsonIgnored, it will be nullified in the web response.
		Long pk = rs.getLong( "pk" );
		Profile p = new Profile();
		p.setPk( pk );
		p.setId( rs.getLong( "id" ) );
		p.setName( rs.getString( "name" ) );
		
		List<Owner> owners = ownerDao.fetchOwnersByProfile( p );
		List<Animal> animals = animalDao.fetchAnimalsByProfile( p );

		p.setOwners( owners );
		p.setAnimals( animals );
		return p;
	}
	
	class ProfileMassResultSetExtractor implements RowMapper<Profile> {
		@Override
		public Profile mapRow( ResultSet rs, int rowNum ) throws SQLException {
			return profileFromResultSet( rs );
		}
	}
	
	public List<Profile> fetchProfiles() {
		String sql = "select * from profiles";
		return npJdbcTemplate.query( sql, new ProfileMassResultSetExtractor() );
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
	
	public List<Profile> fetchSomeProfiles( String nameContains ) {
		String sql = "select * from profiles where name like concat( '%', :containing, '%' ) order by id";
		Map<String, Object> params = new HashMap<>();
		params.put( "containing", nameContains );
		return npJdbcTemplate.query( sql, params, new ProfileMassResultSetExtractor() );
	}

	// ==== Updating profiles
	
	public String modifyProfile( Long id, String name ) {
		String sql = "update profiles set name = :name where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		params.put( "name", name );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to update profile (profile id may not exist)." );
		} else {
			return ( "Successfully modified profile." );  // idem.
		}
	}

	// ==== Adding profiles 
	
	public Profile addProfile( String name ) {
		String sql = "insert into profiles ( id, name ) values ( :id, :name )";
		Long pid = getNextProfileId();
		Map<String, Object> params = new HashMap<>();
		params.put( "id", pid );
		params.put( "name", name );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to add profile." );
		}
		// Alternately, could rearrange to get PK and fill out the profile that way, but a db re-read gives more confidence.
		return fetchProfileById( pid ).get();
	}

	// ==== Deleting profiles
	
	public String deleteProfile( Long id ) {
		String sql = "delete from profiles where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new NoSuchElementException( "General failure in deleting profile." );
		} else {
			return ( "Successfully deleted profile " + id );
		}
	}
	
}
