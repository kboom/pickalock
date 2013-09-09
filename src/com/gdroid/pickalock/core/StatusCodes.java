package com.gdroid.pickalock.core;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.gdroid.pickalock.unused.EnumStatusCode;

public enum StatusCodes implements EnumStatusCode<StatusCodes> {
	
	GRABBED,
	RELEASED,
	ANIMATING;
	
	private final static Set<StatusCodes> values;
	static {
		values = EnumSet.allOf(StatusCodes.class);
	}
	
	public long getValue(){
        return 1 << this.ordinal();
    }

	@Override
	public Collection<StatusCodes> getValues() {
		return values;
	} 
	
}
