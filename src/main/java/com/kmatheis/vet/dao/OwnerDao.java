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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.kmatheis.vet.entity.Owner;
import com.kmatheis.vet.entity.Profile;
import com.kmatheis.vet.exception.IllegalAttemptException;
import com.kmatheis.vet.internal.SqlParams;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OwnerDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	// private IdGenerator ownerIdGen = new IdGenerator( 5001L, "owners", npJdbcTemplate );
	
	private Long noid;
	
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

	
	public Owner addOwnerToProfile( String name, String phone, Profile p ) {
		// Illustrating obtaining the pk of the Owner upon insertion, and thus setting the pk attribute back to the returned Owner
		//   (even though we don't display it).
		SqlParams params = new SqlParams();
		params.sql = "insert into owners( name, phone, id, profile_fk ) values ( :name, :phone, :id, :profile_fk )";
		params.source.addValue( "name", name );
		params.source.addValue( "phone", phone );
		Long aid = getNextOwnerId();
		params.source.addValue( "id", aid );
		params.source.addValue( "profile_fk", p.getPk() );
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int status = npJdbcTemplate.update( params.sql, params.source, keyHolder );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to add owner." );
		}
		Long pk = keyHolder.getKey().longValue();
		return Owner.builder()
				.pk( pk )
				.id( aid )
				.name( name )
				.phone( phone )
				.profileId( p.getId() )
				.build();
	}
	
	private Owner ownerFromResultSet( ResultSet rs ) throws SQLException {
		return Owner.builder()
				.pk( rs.getLong( "pk" ) )
				.id( rs.getLong( "oid" ) )
				.name( rs.getString( "name" ) )
				.phone( rs.getString( "phone" ) )  
				.profileId( rs.getLong( "pid" ) )
				.build();
	}
	
	class OwnerResultSetExtractor implements ResultSetExtractor<Optional<Owner>> {
		@Override
		public Optional<Owner> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			if ( rs.next() ) {  
				return Optional.of( ownerFromResultSet( rs ) );
			}
			return Optional.empty();
		}
	}
	
	public Optional<Owner> fetchOwnerById( Long id ) {
		String sql = "select o.pk, o.id as \"oid\", o.name, o.phone, p.id as \"pid\" from "
					+ "owners o inner join profiles p on o.profile_fk = p.pk "
					+ "where o.id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		return npJdbcTemplate.query( sql, params, new OwnerResultSetExtractor() );
	}
	
	// ==== Owner ID Management
	
	private Optional<Long> fetchLastOwnerId() {
		String sql = "select id from owners order by id desc limit 1";
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
	
	public synchronized Long getNextOwnerId() {
		if ( noid == null ) {
			Optional<Long> opid = fetchLastOwnerId();
			if ( opid.isPresent() ) {
				noid = opid.get() + 1;
			} else {
				noid = 50001L;
			}
		}
		Long out = noid;
		noid = noid + 1;
		return out;
	}
	
	// ==== Delete and Update

	public String deleteOwner( Long oid ) {
		String sql = "delete from owners where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", oid );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new NoSuchElementException( "Owner with id " + oid + " does not exist." );
		} else {
			return ( "Successfully deleted owner " + oid );
		}
	}

	public String modifyOwner( Long opk, String newName, String newPhone, Long newppk ) {
		String sql = "update owners set name = :name, phone = :phone, profile_fk = :ppk where pk = :opk";
		Map<String, Object> params = new HashMap<>();
		params.put( "name", newName );
		params.put( "phone", newPhone );
		params.put( "ppk", newppk );
		params.put( "opk", opk );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new NoSuchElementException( "General failure updating owner." );
		} else {
			return ( "Successfully modified owner." );
		}
	}
}
