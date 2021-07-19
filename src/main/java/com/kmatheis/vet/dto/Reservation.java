package com.kmatheis.vet.dto;

import java.util.Date;

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

	private Long aid;
	private Long rid;
	
	private Date fromdate;  // The big conceit here is that a reservation goes from (say) 2pm fromdate through noon todate,
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
