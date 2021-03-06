package com.gdroid.pickalock.dressing;

import com.gdroid.pickalock.dressing.LockDresser.MyOutfitSet;
import com.gdroid.pickalock.dressing.ObjectDresser.OutfitSet;
import com.gdroid.pickalock.dressing.Outfit.State;

public class CurveDresser<T extends Enum<T> & Wardrobe<T>> extends ObjectDresser<T> {
	
	CurveDresser(WardrobeSet<T> w) {
		super(w);
	}
	
	@Override
	public OutfitSet getSet(String name) {
		return new MyOutfitSet(name);
	}

	public class MyOutfitSet extends ObjectDresser<T>.OutfitSet {

		protected MyOutfitSet(String name) {
			super(new State[] { State.NORMAL }, "curve", name);
		}

	}
	
}
