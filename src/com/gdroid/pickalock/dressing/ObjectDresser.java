package com.gdroid.pickalock.dressing;

import java.util.Iterator;

import com.gdroid.pickalock.dressing.Outfit.State;

public abstract class ObjectDresser<T extends Enum<T> & Wardrobe<T>> {
	private WardrobeSet<T> wardrobe;

	ObjectDresser(WardrobeSet<T> w) {
		wardrobe = w;
	}

	public abstract OutfitSet getSet(String name);

	public class OutfitSet implements Iterable<T> {

		private final State[] stateArr;
		private final String typeName;
		private final String skinName;

		protected OutfitSet(State[] arr, String typeName, String skinName) {
			stateArr = arr;
			this.skinName = skinName;
			this.typeName = typeName;
		}
		
		public int size() {
			return stateArr.length;
		}

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				private int cstate = 0;

				@Override
				public boolean hasNext() {
					if (cstate > stateArr.length - 1)
						return false;
					else return true;
					
				}

				@Override
				public T next() {
					String key = String.format("%s_%s_%s", typeName,
							stateArr[cstate].getName(), skinName);
					T t = wardrobe.get(key);
					if (t == null) {
						String s = String
								.format("Wardrobe '%s' element %s could not been found.",
										typeName, key);
						throw new IllegalStateException(s);
					} else {
						cstate++;
						return t;
					}
				}

				@Override
				public void remove() {
					throw new IllegalStateException(
							"Remove called on non-removable element set.");
				}

			};
		}
		
	}
}
