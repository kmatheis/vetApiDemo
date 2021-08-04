package com.kmatheis.vet.dto;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   // despite @Builder enforcing an AllArgsConstructor
@AllArgsConstructor  // restore @Builder
public class Reservation {

	@NotNull( message = "animal ID ('aid') must exist." )
	private Long aid;
	
	@NotNull( message = "room ID ('rid') must exist." )
	@Min( value = 1, message = "room ID ('rid') must be positive." )
	private Long rid;
	
	@NotNull( message = "fromdate must exist." )
	private Date fromdate;  // The big conceit here is that a reservation goes from (say) 2pm fromdate through noon todate,
	
	@NotNull( message = "todate must exist." )
	private Date todate;    //   so that one res for a room can end on a date and another can start for that same room on that same date. 
	
	private Integer paid;
	
	@JsonFormat( pattern="yyyy-MM-dd", timezone="America/New_York" )  // timezone is important since we're not by default throwing all to UTC... 
	public Date getFromdate() {
		return fromdate;
	}
	
	@JsonFormat( pattern="yyyy-MM-dd", timezone="America/New_York" )  // ...and otherwise we'll have db/server tz sync issues.
	public Date getTodate() {
		return todate;
	}
}
