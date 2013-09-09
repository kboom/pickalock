package com.gdroid.pickalock.utils;

public class AllocationGuard {

	public static boolean guardActive = false;

	public AllocationGuard() {
		/*
		if (guardActive)
			throw new IllegalStateException(
					"Tried to allocate when allocation guard active.");
					*/
	}
}
