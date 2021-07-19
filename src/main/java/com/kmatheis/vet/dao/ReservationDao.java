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

import com.kmatheis.vet.dto.Reservation;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReservationDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	// This could be easier with views but not messing with views in H2 right now.
	private static String colssql = "re.animal_fk as \"apk\", a.id as \"aid\", re.room_fk as \"rpk\", ro.id as \"rid\", re.fromdate, re.todate, re.paid ";
	private static String tablessql = "reservations re inner join animals a on re.animal_fk = a.pk "
										+ "inner join rooms ro on re.room_fk = ro.pk ";

	private Reservation reservationFromResultSet( ResultSet rs ) throws SQLException {
		return Reservation.builder()
				.aid( rs.getLong( "aid" ) )
				.rid( rs.getLong( "rid" ) )
				.fromdate( rs.getDate( "fromdate" ) )
				.todate( rs.getDate( "todate" ) )
				.paid( rs.getInt( "paid" ) )
				.build();
	}
	
	public List<Reservation> fetchReservationsByApk( Long pk ) {
		String sql = "select " + colssql + "from " + tablessql + "where a.pk = :apk";
		Map<String, Object> params = new HashMap<>();
		params.put( "apk", pk );
		return npJdbcTemplate.query( sql, params, 
				new RowMapper<Reservation>() {
					@Override
					public Reservation mapRow( ResultSet rs, int rowNum ) throws SQLException {						
						return reservationFromResultSet( rs );
					}
				}
		);
	}
	
	class CountExtractor implements ResultSetExtractor<Optional<Integer>> {
		@Override
		public Optional<Integer> extractData( ResultSet rs ) throws SQLException, DataAccessException {
			if ( rs.next() ) {  
				return Optional.of( rs.getInt( "count(*)" ) );
			}
			return Optional.empty();
		}
	}
	
	// for single maxcap rooms only (for larger maxcaps, will use more robust sql)
	public boolean isRoomClear( Long rid, String fdstr, String tdstr ) {
		String sql = "select count(*) from " + tablessql + "where ro.id = :rid and not "
						+ "( :fdstr >= re.todate or :tdstr <= re.fromdate )";
		Map<String, Object> params = new HashMap<>();
		params.put( "rid", rid );
		params.put( "fdstr", fdstr );
		params.put( "tdstr", tdstr );
		int count = npJdbcTemplate.query( sql, params, new CountExtractor() ).get(); 
		// log.debug( "in isRoomClear, fdstr={} and tdstr={}", fdstr, tdstr );
		// log.debug( "in isRoomClear, count is {}", count );
		return count == 0;
	}

	public String addReservation( Long apk, Long rpk, String fdstr, String tdstr ) {
		String sql = "insert into reservations values ( :apk, :rpk, :fdstr, :tdstr, 0 )";
		Map<String, Object> params = new HashMap<>();
		params.put( "apk", apk );
		params.put( "rpk", rpk );
		params.put( "fdstr", fdstr );
		params.put( "tdstr", tdstr );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to add reservation." );
		} else {
			return ( "Successfully added reservation." );  
		}
	}
	
	
}
