package com.gdroid.pickalock.dressing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.gdroid.pickalock.R;
import com.gdroid.pickalock.drawing.ObjectAvatar;
import com.gdroid.pickalock.drawing.Shape;
import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.drawing.StrokeShape;
import com.gdroid.pickalock.drawing.Texture;
import com.gdroid.pickalock.drawing.TextureLibrary;
import com.gdroid.pickalock.drawing.TexturedAvatar;
import com.gdroid.pickalock.dressing.ObjectDresser.OutfitSet;
import com.gdroid.pickalock.dressing.Outfit.State;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * Changes visual apperance and sounds of assembled objects. It is to be done
 * without reinstantiating any of them. Does not depend on what game mode is on.
 * 
 * One object can have multiple apperances relecting its internal state.
 * 
 * Different dressers can build different objects.
 * 
 * @author kboom
 * 
 */
public class Dresser<T extends Enum<T> & Wardrobe<T>> {

	private static final int did = SLog.register(Dresser.class);
	static {
		SLog.setTag(did, "Dresser.");
		SLog.setLevel(did, Level.DEBUG);
	}

	public enum Type {
		LOCKPICK, LOCK, CURVE, DOORS, AMBIENT, ANCHOR
	}

	private ShapeFactory sfactory;
	private TextureLibrary tlibrary;
	private Map<Type, ObjectDresser<T>> dresserCol;

	public Dresser(ShapeFactory shapeFactory, TextureLibrary texLibrary, WardrobeSet<T> source) {
		sfactory = shapeFactory;
		tlibrary = texLibrary;
		dresserCol = new HashMap<Type, ObjectDresser<T>>();		
		dresserCol.put(Type.LOCKPICK, new LockpickDresser<T>(source));
		dresserCol.put(Type.LOCK, new LockDresser<T>(source));
		dresserCol.put(Type.CURVE, new CurveDresser<T>(source));
		dresserCol.put(Type.DOORS, new DoorsDresser<T>(source));
		dresserCol.put(Type.AMBIENT, new AmbientObjectDresser<T>(source));
		dresserCol.put(Type.ANCHOR, new AnchorDresser<T>(source));
	}
	
	public Outfit getOutfit(Type type, Map<State, Shape> sh, String name) {
		Outfit.Builder builder = new Outfit.Builder();
		ObjectDresser<T> dresser = dresserCol.get(type);	
		
		for(T t : dresser.getSet(name)) {
			State st = t.getRepresentedState();
			if(!sh.containsKey(st)) continue;			
			Texture tex = new Texture(t.getTextureMapping(tlibrary));			
			TexturedAvatar avatar = new TexturedAvatar(sh.get(st), tex);
			builder.add(st, avatar);
		}		
		return builder.build();
	}
	
	public Outfit getOutfit(Type type, String name) {
		SLog.d(did, String.format("Dressing element of type %s with skin %s...", type.name(), name));
		Outfit.Builder builder = new Outfit.Builder();
		ObjectDresser<T> dresser = dresserCol.get(type);
		ObjectDresser<T>.OutfitSet set = dresser.getSet(name);
		if(set.size() < 1) throw new IllegalArgumentException();
		else SLog.d(did, "Found " + set.size() + " states.");
		for(T t : set) {
			Shape sh = t.getShape(sfactory);
			Texture tex = new Texture(t.getTextureMapping(tlibrary));
			State st = t.getRepresentedState();
			TexturedAvatar avatar = new TexturedAvatar(sh, tex);
			builder.add(st, avatar);
		}
		
		return builder.build();
	}

	protected final TextureLibrary getTextureLibrary() {
		return tlibrary;
	}

	protected final ShapeFactory getShapeFactory() {
		return sfactory;
	}


}
