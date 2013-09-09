package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.AnchorVector;
import com.gdroid.pickalock.core.positioning.JoinedVector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.drawing.ObjectAvatar;
import com.gdroid.pickalock.dressing.Outfit;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * Super class for every object that is visible on the screen. Consists of data
 * which is used to determine object's behaviour and draw it onto the screen.
 * 
 * 
 * There are three data types:
 * <ul>
 * <li>Init(ial)/Stat(ic) - data that does not change at all.</li>
 * <li>Curr(ent) - data that may change at each iteration but is persistent.</li>
 * <li>Diff(erential) - data that is temporary and is forgotten after each
 * iteration.</li>
 * <li>Targ(et) - same af differencial but may be used to create next current.</li>
 * </ul>
 * 
 * Object position is determined by 4 vectors:
 * <ul>
 * <li>Sticky point - this is a global attachment point.</li>
 * <li>Direction vector - this is an unit vector pointing direciton of the
 * object.</li>
 * <li>Rotation vector - this is an unit vector pointing rotation of the object.
 * Orthogonal to direction vector.</li>
 * <li>Size vector - this vector can be used by subclasses to determine all
 * object edges.</li>
 * </ul>
 * and their subtypes according to data type. <b>Curr</b> positions are the ones
 * being static in this iteration. This means that they are fully legal and will
 * not change until the next legal position is provided. They cannnot be also
 * directly accessed by the outside world. All components can operate on a
 * offset-modifiable proxy of this vector, which can be modified by subsequent
 * components. They should be using it both as a data source, alongside with
 * <b>diff</b> positions, and an modification target. If all operations are
 * legal, offset is being changed. If a component detects wrong operation,
 * offset is reset making other components look at pure, current position which
 * was and is legal. At the end of each iteration, offset, if present, is being
 * added to last current position and reset. <b>Diff</b> positions are set up
 * solely by motion components at each iteration start and are not modified till
 * the end of this iteration when they are reset. They do not affect any other
 * position (that is they are not joined with another one).
 * 
 * @author kboom
 * 
 */
public abstract class GameObject extends StateObject<GameObjectStatusCodes> {

	private static final int did = SLog.register(GameObject.class);
	static {
		SLog.setTag(did, "Game object.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	/**
	 * Positions
	 */

	// an anchor is a supportive point for grabbing & modifying direction
	private final AnchorVector anchorPoint;
	// a difference to last valid motion (mostly touch) vector
	private final Vector motionVector;

	private final Vector initStickyPoint;
	private final Vector currStickyPoint;
	private final Vector targStickyPoint;

	private final Vector initDirection;
	private final Vector currDirection;
	private final Vector targDirection;

	private final Vector initRotation;
	private final Vector currRotation;
	private final Vector targRotation;

	private boolean needsRefreshing;

	/**
	 * Outfit and size
	 */

	private Outfit outfit;
	private final Vector size;
	private final Vector gravity;

	protected GameObject() {
		super(GameObjectStatusCodes.class);

		motionVector = system.vector.create(0, 0);

		initStickyPoint = system.vector.create(0, 0, 0);
		currStickyPoint = system.vector.create(0, 0, 0);
		targStickyPoint = system.vector.createJoined(currStickyPoint);

		initDirection = system.vector.create(1, 0, 0);
		currDirection = system.vector.create(1, 0, 0);
		targDirection = system.vector.createJoined(currDirection);

		initRotation = system.vector.create(0, 1, 0);
		currRotation = system.vector.create(0, 1, 0);
		targRotation = system.vector.createJoined(currRotation);

		anchorPoint = system.vector
				.createAnchor(currStickyPoint, currDirection);
		size = system.vector.create(1f, 1f, 1f);
		gravity = system.vector.create();
		needsRefreshing = true;
	}

	public AnchorVector getAnchor() {
		return anchorPoint;
	}

	/**
	 * Only motion components should modify this vector. This is where are
	 * changes originate from.
	 * 
	 * @return
	 */
	public final Vector getMotion() {
		return motionVector;
	}

	/**
	 * Use to obtain the global attachment point of this object. Should be used
	 * both as information source and modification target. If this vector is not
	 * valid, it should be reset.
	 * 
	 * 
	 * @return
	 */
	public final Vector getStickyPoint() {
		return targStickyPoint;
	}

	/**
	 * Extracts any changes applied to sticky position vector in this iteration.
	 * This changes are automatically added to current local position vector on
	 * iteration end.
	 * 
	 * @return
	 */
	public final Vector getStickyPointMod() {
		return ((JoinedVector) targStickyPoint).getOffset();
	}

	/**
	 * Use to obtain a direction of this object. Should be used both as
	 * information source and modification target. If this vector is not valid,
	 * it should be reset. This vector must be unit.
	 * 
	 * In most cases it is reasonable call rotate to modify it instead of
	 * applying any changes yourself.
	 * 
	 * @return
	 */
	public final Vector getDirection() {
		return targDirection;
	}

	/**
	 * 
	 * @param dinc
	 * @param dazi
	 * @return
	 */
	public final Vector faceDirection(float dinc, float dazi) {
		targDirection.rotate(dinc, dazi);
		// preserve orthogonality
		targRotation.rotate(dinc, dazi);
		return targDirection;
	}

	/**
	 * Extracts changes applied to direction vector in this iteration. This
	 * changes are automatically added to current local position vector on
	 * iteration end.
	 * 
	 * @return
	 */
	public final Vector getDirectionMod() {
		return ((JoinedVector) targDirection).getOffset();
	}

	/**
	 * Use to obtain a rotation of this object. Should be used both as
	 * information source and modification target. If this vector is not valid,
	 * it should be reset. This vector must be unit.
	 * 
	 * In most cases it is reasonable to call rotate to modify it instead of
	 * applying any changes yourself.
	 * 
	 * 
	 * @return
	 */
	protected final Vector getRotation() {
		return targDirection;
	}

	/**
	 * This should be used to rotate an object. It rotates a rotation vector
	 * around direction vector so they stay orthogonal.
	 * 
	 * @param deg
	 * @return
	 */
	public final Vector rotate(float deg) {
		targRotation.rotate(deg, targDirection);
		return targRotation;
	}

	/**
	 * Extracts changes applied to rotation vector in this iteration. This
	 * changes are automatically added to current local position vector on
	 * iteration end.
	 * 
	 * @return
	 */
	public final Vector getRotationMod() {
		return ((JoinedVector) targRotation).getOffset();
	}

	/*
	 * Outfit and size related
	 */

	/**
	 * 
	 * @return
	 */
	public final Vector getSize() {
		return size;
	}

	/**
	 * Use to obtain a view. Tricky thing is that real field is hidden, so even
	 * if complex field is returned, it cannot be changed.
	 * 
	 * @return
	 */
	public ObjectAvatar getView() {
		return outfit;
	}

	public final void invalidate() {
		needsRefreshing = true;
	}

	public Vector getGravityVector() {
		return gravity;
	}

	public final boolean needsRefreshing() {
		return needsRefreshing;
	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);
		outfit.update(timeDelta, this);
		super.commitStatusChanges();
		needsRefreshing = false;

		currStickyPoint.add(getStickyPointMod());
		currDirection.add(getDirectionMod());
		currRotation.add(getRotationMod());

		motionVector.reset();
		targStickyPoint.reset();
		targDirection.reset();
		targRotation.reset();
	}

	@Override
	public void reset() {

		currStickyPoint.set(initStickyPoint);
		SLog.e(did, "INIT INIT direction: " + initDirection.printSimple());
		currDirection.set(initDirection);
		currRotation.set(initRotation);

		motionVector.reset();
		targStickyPoint.reset();
		targDirection.reset();
		targRotation.reset();

		needsRefreshing = true;
		
		SLog.e(did, String.format("Object %d initial state: \n%s", super.getIdentifier(), printPositioningInfo()));
		
		
		
		super.reset();

	}

	/**
	 * Prints simple positioning info consisting of four vectors:
	 * <ul>
	 * <li>Touch point</li>
	 * <li>Sticky position</li>
	 * <li>Direction</li>
	 * <li>Rotation</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String printPositioningInfo() {
		return String
				.format("Position summary for object %d:\nTouch:%s,\nSticky:%s\tDirection:%s,\tRotation:%s,\nAnchor:%s",
						super.getIdentifier(), motionVector.printSimple(),
						targStickyPoint.printSimple(),
						targDirection.printSimple(),
						targRotation.printSimple(), getAnchor().printSimple());
	}

	/**
	 * Enforce implementing a builder.
	 */
	public abstract static class Builder<B extends Builder<B, T>, T extends GameObject> {

		private Vector initDirection;
		private Vector initStickyPoint;
		private Vector initRotation;
		private Vector initAnchorPoint;
		private Outfit outfit;
		private Vector size;

		public Builder() {		
		}

		protected final SystemRegistry getSystem() {
			return BaseObject.system;
		}

		/*
		 * Methods underneath can be overriden by the concrete builder and
		 * called in overriden method to provide default values for someone who
		 * will construct objects or it can just be used.
		 */

		public void setInitStickyPoint(Vector vector) {
			initStickyPoint = getSystem().vector.create();
			initStickyPoint.set(vector);
		}

		public void setInitDirection(Vector pos) {
			initDirection = getSystem().vector.create();
			initDirection.set(pos);
		}

		public void setInitRotation(Vector rot) {
			initRotation = getSystem().vector.create();
			initRotation.set(rot);
		}

		public void setInitAnchorPoint(Vector v) {
			initAnchorPoint = getSystem().vector.create();
			initAnchorPoint.set(v);
		}

		public void setSize(Vector s) {
			size = getSystem().vector.create();
			size.set(s);
		}

		public void attachBody(Outfit body) {
			this.outfit = body;
		}

		/*
		 * Methods underneath are common among all game objects. They must be
		 * overriden in a subclass.
		 */

		/**
		 * Attaches durability component to this game object. Should be
		 * implemented by the first builder in chain capable of accessing
		 * positioning field of the object created. Next builders in chain
		 * should extend it further, creating components themselves (using
		 * fields provided by super class) and using super function to push them
		 * into the created object.
		 * 
		 * @param t
		 */
		protected abstract void attachPositioning(T t);

		/**
		 * Attaches durability component to this game object. Should be
		 * implemented by the first builder in chain capable of accessing
		 * renderer field of the object created. Next builders in chain should
		 * extend it further, creating components themselves (using fields
		 * provided by super class) and using super function to push them into
		 * the created object.
		 * 
		 * @param t
		 */
		protected abstract void attachRender(T t);

		/**
		 * Used to get instance of template object, should be overriden and made
		 * final by concrete builder (last in chain).
		 * 
		 * @return
		 */
		protected abstract T create();

		/**
		 * Enforces right construction order. This is important as components
		 * can depend on some fields of game object that must be set earlier by
		 * another one.An builder in chain that can access {T} private variables
		 * should override proper method and set them using provided object of
		 * {T} class. This shall be overriden by next builder in chain, called
		 * first, and checked if returned object has needed values (handled by
		 * this particular builder).
		 */
		public final T construct() {

			T t = create();

			if (initStickyPoint != null) {
				t.initStickyPoint.set(initStickyPoint);
			}
			if (initAnchorPoint != null) {
				t.anchorPoint.set(initAnchorPoint);
				float levlen = Math.abs(initAnchorPoint.getR3()-initStickyPoint.getR3());
				t.anchorPoint.setLeverLength(levlen);
				SLog.e(did, "Anchor point to be set:  " + initAnchorPoint.printSimple());
				t.initDirection.set(t.anchorPoint.getDirection());
				SLog.d(did,
						"Initial anchor point (" + t.anchorPoint.print()
								+ ") resulted in initial direction ("
								+ t.initDirection.print() + ")");
			} else if (initDirection != null) {
				t.initDirection.set(initDirection);
			}
			if (initRotation != null) {
				t.initRotation.set(initRotation);
			}
			if (size != null) {
				t.size.set(size);
			}

			t.outfit = outfit;

			onPreAttachment(t);
			attachPositioning(t);
			attachRender(t);
			onPostAttachment(t);

			SLog.d(did, "Reseting newly created object...");
			t.reset();

			return t;
		}

		/**
		 * Called before components are attached to the object {@code t}. This
		 * should be used to attach additional non-standard components.
		 * 
		 * @param t
		 */
		protected void onPreAttachment(T t) {
		}

		/**
		 * Called after components are attached to the object {@code t}
		 * 
		 * @param t
		 */
		protected void onPostAttachment(T t) {
		}

	}

}
