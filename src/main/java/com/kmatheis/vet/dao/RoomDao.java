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

import com.kmatheis.vet.dao.AnimalDao.AnimalResultSetExtractor;
import com.kmatheis.vet.entity.Animal;
import com.kmatheis.vet.entity.Room;

@Component
public class RoomDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	private Room roomFromResultSet( ResultSet rs ) throws SQLException {
		return Room.builder()
				.pk( rs.getLong( "pk" ) )
				.id( rs.getLong( "id" ) )
				.name( rs.getString( "name" ) )
				.maxcap( rs.getLong( "maxcap" ) )
				.cost( rs.getDouble( "cost" ) )
				.build();
	}
	
	public List<Room> fetchRooms() {
		String sql = "select * from rooms";
		return npJdbcTemplate.query( sql, 
				new RowMapper<Room>() {
					@Override
					public Room mapRow( ResultSet rs, int rowNum ) throws SQLException {						
						return roomFromResultSet( rs );
					}
				}
		);
	}
	
	class RoomResultSetExtractor implements ResultSetExtractor<Optional<Room>> {
		@Override
		public Optional<Room> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			if ( rs.next() ) {  
				return Optional.of( roomFromResultSet( rs ) );
			}
			return Optional.empty();
		}
	}
	
	public Optional<Room> fetchRoomById( Long id ) {
		String sql = "select * from rooms where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", id );
		return npJdbcTemplate.query( sql, params, new RoomResultSetExtractor() );
	}
}
