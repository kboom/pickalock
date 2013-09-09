package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.drawing.MotionObserver;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * Component responsible for capturing motion and validating its type. If a
 * motion is valid, then it modifies targetPosition of the host object. Calls
 * invalidate on the host. This usually makes other components run.
 * 
 * Generally operates on differences. When a touch is discovered motion vector
 * has absolute value for a short period of time until move is detected, then it
 * gets converted to a difference and stays that way.
 * 
 * 
 * @author kboom
 * 
 */
public abstract class MotionComponent extends Component implements
		MotionObserver {

	private static final int did = SLog.register(MotionComponent.class);
	static {
		SLog.setTag(did, "Motion Component.");
		SLog.setLevel(did, Level.VERBOSE);
	}
//
	private final Vector absMotion = system.vector.create();
	private final Vector absMotionNormalized = system.vector.create();
	private final Vector diffMotion = system.vector.create();

	private boolean wasMoved;
	private int pointer;

	@Override
	public void reset() {
		wasMoved = false;
		absMotion.reset();
		diffMotion.reset();
		pointer = -1;
	}

	public final Vector getDiffMotion() {
		return diffMotion;
	}
		
	public final Vector getAbsMotion() {
		return absMotion;
	}
	
	public final Vector getNormAbsMotion() {
		return absMotionNormalized;
	}

	public final boolean isMoved() {
		return wasMoved;
	}

	public final void setMoved(boolean state) {
		wasMoved = state;
	}

	public int getPointer() {
		return pointer;
	}

	public void setPointer(int id) {
		pointer = id;
	}

	/**
	 * Translates a touch vector to match a camera position.
	 * 
	 * @param v
	 */
	public void translateByCamera(Vector v) {
		v.multiply(3, 3, 1);
		SLog.e(did, "V: " + v.printSimple());
	}

	@Override
	public final void onDownTouch(int x, int y, int pointerId) {
		if (pointer != -1)
			return;

		SLog.e(did, "Touched down, last motion = " + absMotion.printSimple());
		
		synchronized (absMotion) {
			absMotion.setCartesian(x, y, 3);
			absMotionNormalized.set(absMotion);
			system.vector.normalize(absMotionNormalized);
			translateByCamera(absMotionNormalized);
			
			if (touchedDown()) {
				pointer = pointerId;
				wasMoved = true;
				SLog.d(did, "Grab " + pointer + " hold.");
			}

		}

		
	}

	@Override
	public void onUpTouch(int x, int y, int pointerId) {
		if (pointerId == pointer) {
			SLog.d(did, "Grab " + pointer + " released.");
			pointer = -1;
			wasMoved = true;
			touchedUp();

			absMotion.reset();
			absMotionNormalized.reset();
			diffMotion.reset();
		}
	}

	public final void onMoveTouch(int historicalX, int historicalY,
			int pointerId) {
		if (pointerId != pointer)
			return;
		
		float nxn, nyn;
		float oldxn = absMotionNormalized.getX();
		float oldyn = absMotionNormalized.getY();
		
		synchronized (absMotion) {
			//x = historicalX - absMotion.getX();
			//y = historicalY - absMotion.getY();
			absMotion.setCartesian(historicalX, historicalY, 0);	
			absMotionNormalized.set(absMotion);
			system.vector.normalize(absMotionNormalized);
			translateByCamera(absMotionNormalized);			
			
			nxn = absMotionNormalized.getX();
			nyn = absMotionNormalized.getY();
		}

		synchronized (diffMotion) {
			float nx = nxn-oldxn;
			float ny = nyn-oldyn;
			SLog.e(did, "DIFFF: " + nx + ", " + ny);
			diffMotion.setCartesian(nx, ny, 0);
			translateByCamera(diffMotion);//
		}		
		
		wasMoved = true;
		moved();
	}

	protected void moved() {
		// hook
	}

	protected abstract void touchedUp();

	protected abstract boolean touchedDown();

	public static MotionComponent getNeutral() {
		return new MotionComponent() {

			@Override
			protected void touchedUp() {
				// TODO Auto-generated method stub
			}

			@Override
			protected boolean touchedDown() {
				return true;
			}

			@Override
			protected void update(float timeDelta, BaseObject parent) {

			}

		};
	}

}
