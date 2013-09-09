package com.gdroid.pickalock.core.positioning;

import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * 
 * @author kboom
 * 
 */
public class RadialPosOperation extends PositioningOperation {

	private static final int did = SLog.register(RadialPosOperation.class);
	static {
		SLog.setTag(did, "Radial Positioning Operation.");
		SLog.setLevel(did, Level.DEBUG);
	}

	private final AnchorVector source;
	private final Vector target;
	private final Vector center;

	/**
	 * 
	 * @param target
	 *            vector to be changed
	 * @param source
	 *            source of data
	 * @param anchor
	 *            sphere center
	 * @param size
	 *            size of an element (pomyśleć nad tym bo wcale z nie musi być...)
	 */
	public RadialPosOperation(Vector target, AnchorVector source, Vector center) {
		super(target, source);
		this.source = source;
		this.target = target;
		this.center = center;
	}

	@Override
	public void perform() {
		
		SLog.d(did, "Radial positioning: " + super.source.print());
		
		// get z from (x-x0)^2 + (y-y0)^2 + (z-z0)^2 = r^2
		final float r = source.getLeverLength();
		final float z0 = center.getZ();
		final float z2 = (float) Math.pow(z0,2);
		
		// (x-x0)^2
		final float x2 = (float) Math.pow(source.getX() - center.getX(), 2);
		// (y-y0)^2
		final float y2 = (float) Math.pow(source.getY() - center.getY(), 2);
		
		// r^2 - z0^2 -x^2 -y^2
		final float c = (float) (Math.pow(r, 2) - (z2 + x2 + y2));
			
		// quadrant equation, 
		// z^2 + 2*z0*z - c = 0
		final float delta = 4*z2 + 4*c;
		
		// get positive solution
		float z = (float) (-z0 + Math.sqrt(delta) / 2);
		
		// finally add fake "z"
		target.add(0, 0, z);
		
		SLog.d(did, "Radial positioning effect: " + super.target.print());
		
	}

}
