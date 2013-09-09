package com.gdroid.pickalock.dressing;

import com.gdroid.pickalock.drawing.Shape;
import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.drawing.Texture;
import com.gdroid.pickalock.drawing.TextureLibrary;
import com.gdroid.pickalock.drawing.TextureMapping;
import com.gdroid.pickalock.dressing.Outfit.State;

public interface Wardrobe<E extends Enum<E>> {
	public TextureMapping getTextureMapping(TextureLibrary l);
	public Shape getShape(ShapeFactory f);
	public State getRepresentedState();
	public String getName();
}
