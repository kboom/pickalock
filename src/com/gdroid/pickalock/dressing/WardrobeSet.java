package com.gdroid.pickalock.dressing;

import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class WardrobeSet<E extends Enum<E> & Wardrobe<E>> {

	private static final int did = SLog.register(WardrobeSet.class);
	static {
		SLog.setTag(did, "Wardrobe Set.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	Class<E> hostedEnum;

	public WardrobeSet(Class<E> enumType) {
		hostedEnum = enumType;
		SLog.d(did, "Selected wardrobe has "
				+ enumType.getEnumConstants().length + " members.");
	}

	public E get(String id) {
		E[] members = hostedEnum.getEnumConstants();
		for (int i = 0; i < members.length; i++) {
			if (members[i].getName().equalsIgnoreCase(id))
				return members[i];
		}
		return null;
	}

}
