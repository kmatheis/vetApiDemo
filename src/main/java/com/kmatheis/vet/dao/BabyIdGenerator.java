package com.kmatheis.vet.dao;

public class BabyIdGenerator {

	Long id;
	
	public BabyIdGenerator( Long latestExistingId ) {
		this.id = next( latestExistingId );
	}
	
	// could do many things here
	private Long next( Long id ) {
        return id + 1;
    }
    
    public synchronized Long getNextId() {
        Long out = id;
        id = next( id );
        return out;
    }
}
