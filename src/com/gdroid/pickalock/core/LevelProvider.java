package com.gdroid.pickalock.core;

/**
 * Extracts data from external file (for now lets assume it's already in the
 * array) and provides necessary paramters for both game objects and systems to
 * being set up. As levels are the same in all game modes, parameters are also
 * the same but they can be used differently. Eg. there can be a completion time
 * specified, but it will not be used in Sandbox mode whereas it will be used in
 * TimeAttack mode.
 * 
 * That "use" is defined by Concrete GameObjectFactory + SystemRegistry pair.
 * 
 * What's more, number of game objects can vary depending on level and all other
 * systems can handle any number of game objects. As this "loading" happens
 * outside game flow, all game objects are rebuilt on level change. Generally no
 * pooling is needed.
 * 
 * @author kboom
 * 
 */
public class LevelProvider {

	public static class GameObjectParameters {
		
	}
}
