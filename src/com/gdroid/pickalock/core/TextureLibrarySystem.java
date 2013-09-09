package com.gdroid.pickalock.core;

import com.gdroid.pickalock.drawing.TextureLibrary;

/**
 * Adapter. To be used only inside the system for dynamic tasks.
 * @author kboom
 *
 */
public class TextureLibrarySystem extends BaseObject {

	private TextureLibrary library;
	
	public TextureLibrarySystem(TextureLibrary library) {
		this.library = library;
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);
	}

}
