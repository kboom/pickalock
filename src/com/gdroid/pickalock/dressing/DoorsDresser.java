package com.gdroid.pickalock.dressing;

import com.gdroid.pickalock.dressing.CurveDresser.MyOutfitSet;
import com.gdroid.pickalock.dressing.ObjectDresser.OutfitSet;
import com.gdroid.pickalock.dressing.Outfit.State;

public class DoorsDresser<T extends Enum<T> & Wardrobe<T>> extends ObjectDresser<T> {

	DoorsDresser(WardrobeSet<T> w) {
		super(w);
	}
	
	@Override
	public OutfitSet getSet(String name) {
		return new MyOutfitSet(name);
	}

	public class MyOutfitSet extends ObjectDresser<T>.OutfitSet {

		protected MyOutfitSet(String skinName) {
			super(new State[] { State.NORMAL }, "doors", skinName);
		}

	}
	
}
