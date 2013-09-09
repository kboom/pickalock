package com.gdroid.pickalock.core;

/**
 * Je��li stan niesie za sob�� jakie�� pola, mo��e metody,
 * to pomy��le�� nad dopisaniem w to enum with visitor pattern.
 * @author kboom
 *
 */
public enum GameObjectStatusCodes {
	GRABBED,
	MOVED,
	ANIMATING,
	DAMAGED,
	MOVE_ACCEPTED,
	MOVE_REJECTED, 
	RELEASED,
	VISIBLE,
	HIDDEN
}
