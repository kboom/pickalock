package com.gdroid.pickalock.dressing;

import java.util.HashMap;
import java.util.Map;

import com.gdroid.pickalock.R;
import com.gdroid.pickalock.drawing.Shape;
import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.drawing.SmartPoint;
import com.gdroid.pickalock.drawing.Texture;
import com.gdroid.pickalock.drawing.TextureLibrary;
import com.gdroid.pickalock.drawing.TextureMapping;
import com.gdroid.pickalock.dressing.Outfit.State;
import com.gdroid.pickalock.utils.SLog;

public enum StandardWardrobe implements Wardrobe<StandardWardrobe> {

	LOCKPICK_NORMAL_1000("lockpick_normal_simple") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			Shape sha = f.createCube(new float[][] { { 1f, 0f, 0f, 1f },
					{ 0f, 1f, 0f, 1f }, { 0f, 0f, 0f, 1f }, { 1f, 1f, 0f, 1f },
					{ 0f, 1f, 1f, 1f }, { 1f, 0f, 1f, 1f } });
			sha.useColor();
			return sha;
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}

	},

	LOCKPICK_BROKEN_1000("lockpick_broken_simple") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			return f.createCube(null);
		}

		@Override
		public State getRepresentedState() {
			return State.BROKEN;
		}

	},

	LOCK_NORMAL_1000("lock_normal_simple") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			Shape s = f.createCube(new float[][] { { 1f, 0f, 0f, 1f },
					{ 0f, 1f, 0f, 1f }, { 0f, 0f, 1f, 1f }, { 1f, 1f, 0f, 1f },
					{ 0f, 1f, 1f, 1f }, { 1f, 0f, 1f, 1f } });
			s.useColor();
			return s;
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}

	},

	CURVE_NORMAL_1000("curve_normal_simple") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			throw new IllegalStateException(
					"Curves must provide predefined shapes.");
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}

	},

	DOORS_NORMAL_1000("doors_normal_simple") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			final SmartPoint[] points = new SmartPoint[] {
					new SmartPoint(-1f, 1f, 0f), new SmartPoint(1f, 1f, 0f),
					new SmartPoint(-1f, -1f, 0f), new SmartPoint(1f, -1f, 0f), };
			return f.createGrid(points, 2);
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}

	},

	AMBIENT_NORMAL_1000("ambient_normal_marker") {

		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			float r = (float) Math.random();
			Shape s = f.createCube(new float[][] { { 1f, r, r, 1f },
					{ r, 1f, 1f, 1f }, { 1f, r, 0f, 1f }, { 1f, 1f, r, 1f },
					{ r, r, 1f, 1f }, { r, 1f, r, 1f } });
			s.useColor();
			return s;
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}

	},
	
	ANCHOR_NORMAL_1000("anchor_normal_simple") {
		
		@Override
		public TextureMapping getTextureMapping(TextureLibrary l) {
			return l.allocateTexture(R.drawable.android);
		}

		@Override
		public Shape getShape(ShapeFactory f) {
			float r = (float) Math.random();
			Shape s = f.createCircle2(1f, 100, null);
			s.useColor();
			return s;
		}

		@Override
		public State getRepresentedState() {
			return State.NORMAL;
		}
		
	};

	private final String id;

	StandardWardrobe(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return id;
	}

}
