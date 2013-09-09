package com.gdroid.pickalock.dressing;

import com.gdroid.pickalock.dressing.Outfit.State;

public class LockpickDresser<T extends Enum<T> & Wardrobe<T>> extends
		ObjectDresser<T> {

	LockpickDresser(WardrobeSet<T> w) {
		super(w);
	}
	
	@Override
	public OutfitSet getSet(String name) {
		return new MyOutfitSet(name);
	}

	public class MyOutfitSet extends ObjectDresser<T>.OutfitSet {

		protected MyOutfitSet(String name) {
			super(new State[] { State.NORMAL, State.BROKEN }, "lockpick", name);
		}

	}
	
}
