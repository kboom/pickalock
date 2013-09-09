package com.gdroid.pickalock.core;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.positioning.PositioningOperation;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.projection.ProjectionOperation;
import com.gdroid.pickalock.drawing.GLRenderer;
import com.gdroid.pickalock.drawing.Renderable;
import com.gdroid.pickalock.drawing.VBOSupportable;
import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.utils.SLog;

/**
 * Bucket for render operations. Apply both projection operations and
 * positioning operations which yet should be used on <strong>drawing positions
 * only</strong>.
 * 
 * @author kboom
 * 
 */
public class RenderComponent extends Component implements Renderable,
		VBOSupportable {

	// rotation angle about X-axis (pitch) - rotate YZ
	// rotation angle about Y-axis (yaw) - rotate XZ
	// rotation angle about Z-axis (roll) - rotate XY

	private static final int did = SLog.register(RenderComponent.class);
	static {
		SLog.setTag(did, "Render Component.");
	}

	private GameObject host;
	private final FixedSizeArray<ProjectionOperation> renderOperations = new FixedSizeArray<ProjectionOperation>(
			5);

	public RenderComponent() {

	}

	/**
	 * Adds a new operation to be performed before rendering. Note that first
	 * operation to be added is actually the last operation to be performed.
	 * 
	 * @param operation
	 */
	public void add(ProjectionOperation operation) {
		renderOperations.add(operation);
	}
	

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		host = (GameObject) parent;
		// if something happens we must reload (eg. ObjectAvatar may have been
		// changed)
		// this approach is good because actually we introduce some pooling

		// for now do it all the time (get rid of it later)!
		if (host.checkFlag(GameObjectStatusCodes.HIDDEN))
			system.render.unschedule(this, host.getView());
		else if (!host.checkFlag(GameObjectStatusCodes.VISIBLE)) {
			system.render.schedule(this, host.getView());
			host.raiseFlag(GameObjectStatusCodes.VISIBLE);
		}
	}

	@Override
	public void reset() {

	}

	// maybe not?

	@Override
	public void generateHardwareBuffers(GL10 gl) {
		host.getView().generateHardwareBuffers(gl);
	}

	@Override
	public void invalidateHardwareBuffers() {
		host.getView().invalidateHardwareBuffers();
	}

	@Override
	public void releaseHardwareBuffers(GL10 gl) {
		host.getView().releaseHardwareBuffers(gl);
	}

	@Override
	public void beginDrawing(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(GL10 gl) {
		if (!super.isActive())
			return;

		// apply projections
		for (ProjectionOperation ro : renderOperations) {
			ro.project(gl);
		}

		// chyba jednak kolejność jest taka jak zapisana
		Vector size = host.getSize();
		gl.glScalef(size.getX(), size.getY(), size.getZ());
		// this is where actual rendering takes place
		host.getView().draw(gl);

	}

	@Override
	public void endDrawing(GL10 gl) {
		// TODO Auto-generated method stub

	}

}
