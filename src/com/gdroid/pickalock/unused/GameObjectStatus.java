package com.gdroid.pickalock.unused;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum flag manager. Every instance of game object possesses one. It allows its
 * components to comunicate.
 * 
 * @author kboom
 * 
 * @param <T>
 */
public class GameObjectStatus<T extends Enum<T> & EnumStatusCode<T>> {

	private final Set<T> flags;

	public GameObjectStatus(Class<T> type) {
		flags = EnumSet.noneOf(type);
	}

	public void raise(T flag) {
		if(!flags.contains(flag))
			flags.add(flag);
	}

	public boolean checkIfSet(final T flag) {
		return flags.contains(flag);
	}
	
}
