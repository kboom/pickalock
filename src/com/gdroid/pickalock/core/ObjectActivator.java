package com.gdroid.pickalock.core;

import com.gdroid.pickalock.drawing.BufferLibrary;
import com.gdroid.pickalock.drawing.TextureLibrary;
import com.gdroid.pickalock.pooling.FixedSizeArray;

/**
 * This is where the objects are actually made active (or inactive). All objects
 * are created elsewhere and do not get gc. Textures are (re)loaded and so on.
 * 
 * Objects that are
 * 
 * @author kboom
 * 
 */
public class ObjectActivator extends BaseObject {

	public static final int MAX_INACTIVE_HOLD = 20;
	
	private final FixedSizeArray<GameObject> inactiveObjects;
	private final BufferLibrary bufferLibrary;
	private final TextureLibrary textureLibrary;
	
	public ObjectActivator(TextureLibrary texLibrary, BufferLibrary bufLibrary) {
		textureLibrary = texLibrary;
		bufferLibrary = bufLibrary;
		inactiveObjects = new FixedSizeArray<GameObject>(MAX_INACTIVE_HOLD);
	}
	
	public void add(GameObject object) {
		inactiveObjects.add(object);
		
	}
	
	public void remove(GameObject object) {
		inactiveObjects.remove(object);
	}
	
	public boolean activate(GameObject object) {
		return true;
	}
	
	public boolean deactivate(GameObject object) {
		return true;
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
