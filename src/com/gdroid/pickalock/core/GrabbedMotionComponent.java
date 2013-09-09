package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.validation.GrabValidator;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class GrabbedMotionComponent extends MotionComponent {

	private static final int did = SLog.register(GrabbedMotionComponent.class);
	static {
		SLog.setTag(did, "Grabbed motion component.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private Vector dummy = system.vector.create();
	private GameObject host;
	private GrabValidator validator;
	private final Vector anchorPoint;
	private final Vector difference;
	private boolean isGrabbed;

	public GrabbedMotionComponent(Vector target, Vector source, GrabValidator validator) {
		difference = target;
		anchorPoint = source;
		this.validator = validator;
		reset();
	}

	@Override
	protected boolean touchedDown() {
		synchronized (anchorPoint) {
			SLog.d(did,
					"Checking if grab is valid; anchor = "
							+ anchorPoint.printSimple() + ", abs motion = "
							+ getNormAbsMotion().printSimple());
			
			
			isGrabbed = validator.isGrabbed(anchorPoint, getNormAbsMotion());
		}
		if (isGrabbed) {
			host.raiseFlag(GameObjectStatusCodes.GRABBED);
			return true;
		} else
			return false;
	}

	@Override
	protected void touchedUp() {
		host.hideFlag(GameObjectStatusCodes.GRABBED);
		isGrabbed = false;
	}

	/**
	 * Methods in update methods are launched from one thread so they can and
	 * should use "now" methods. They still can use scheduling if they want to
	 * delay something.
	 */
	@Override
	protected void update(float timeDelta, BaseObject parent) {
		host = (GameObject) parent;

		// object was released after it had been moved
		if (!isGrabbed && isMoved()) {
			host.hideFlag(GameObjectStatusCodes.MOVED);
			// notify everyone it has been just released
			host.raiseFlag(GameObjectStatusCodes.RELEASED);
			// make sure its gone after everyone gets it
			host.scheduleFlagHide(GameObjectStatusCodes.RELEASED);
			// no motion is present now
			setMoved(false);
			return;
		}

		// object was neither grabbed nor moved
		if (!isGrabbed)
			return;

		// object was moved and its still grabbed
		if (isMoved())
			host.raiseFlag(GameObjectStatusCodes.MOVED);

		Vector diff = getDiffMotion();//
		synchronized (diff) {
			SLog.d(did,
					host.getIdentifier() + ": Got diff: "
							+ diff.print() + " with offset "
							+ system.vector.getOffset().print());
			difference.set(diff); // set difference vector
		}
		host.invalidate();

		// MOVE flag will expire when this update cycle ends
		host.scheduleFlagHide(GameObjectStatusCodes.MOVED);
		setMoved(false);
	}

	@Override
	public void reset() {
		super.reset();
		isGrabbed = false;
	}

}
