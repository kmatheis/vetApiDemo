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
import com.kmatheis.vet.entity.Comment;
import com.kmatheis.vet.entity.Type;
import com.kmatheis.vet.exception.IllegalAttemptException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommentDao {

	@Autowired
	private NamedParameterJdbcTemplate npJdbcTemplate;
	
	@Autowired
	private AnimalDao animalDao;
	
	private BabyIdGenerator idGen;
	
	// ==== Profile ID management
	
	private Optional<Long> fetchLastCommentId() {
		String sql = "select id from comments order by id desc limit 1";
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
	
	public synchronized Long getNextCommentId() {
		if ( idGen == null ) {
			idGen = new BabyIdGenerator( fetchLastCommentId().orElse( 50000L ) );
		}
		return idGen.getNextId();
	}
	
	// ==== Fetching
	
	public List<Comment> fetchCommentsByAnimal( Animal a ) {
		String sql = "select * from comments where animal_fk = :animal_fk";
		Map<String, Object> params = new HashMap<>();
		params.put( "animal_fk", a.getPk() );
		
		return npJdbcTemplate.query( sql, params, 
				new RowMapper<Comment>() {
					@Override
					public Comment mapRow( ResultSet rs, int rowNum ) throws SQLException {
						return Comment.builder()
								.pk( rs.getLong( "pk" ) )
								.id( rs.getLong( "id" ) )
								.ondate( rs.getTimestamp( "ondate" ) )
								// .ondate( rs.getDate( "ondate" ) )
								.type( Type.valueOf( rs.getString( "type" ) ) )
								.comment( rs.getString( "comment" ) )
								.animalId( a.getId() )
								.build();
					}
				}
		);
	}

	// ==== Adding comment
	
	public Animal addCommentToAnimal( Comment comment, Long apk, Long aid ) {
		String sql = "insert into comments ( id, ondate, type, comment, animal_fk ) values ( :id, :ondate, :type, :comment, :animal_fk )";
		Long cid = getNextCommentId();
		Map<String, Object> params = new HashMap<>();
		params.put( "id", cid );
		params.put( "ondate", comment.getOndate() );
		params.put( "type", comment.getType().toString() );
		params.put( "comment", comment.getComment() );
		params.put( "animal_fk", apk );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new IllegalAttemptException( "General failure to add comment." );
		}
		// Verified in service, so it exists here. Also forcing a db re-read to generate utmost confidence that the change was made.
		Animal out = animalDao.fetchAnimalById( aid ).get();
		return out;
	}
	
	// ==== Deleting comment

	public Animal deleteComment( Long aid, Long cid ) {
		String sql = "delete from comments where id = :id";
		Map<String, Object> params = new HashMap<>();
		params.put( "id", cid );
		int status = npJdbcTemplate.update( sql, params );
		if ( status == 0 ) {
			throw new NoSuchElementException( "General failure in deleting comment." );
		}
		return animalDao.fetchAnimalById( aid ).get();
	}
	
}
