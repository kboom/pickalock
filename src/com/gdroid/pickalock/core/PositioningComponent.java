package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.PositioningOperation;
import com.gdroid.pickalock.core.validation.RunningCondition;
import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.utils.SLog;

/**
 * Manager of positioning operations performed on the game object's vectors. Do
 * not know which vectors are being modified. What it does know is types of
 * operations and when they should be launched.
 * 
 * Component that may modify valid, current position according to past state. If
 * so, it obtains boolean lock on the object to prevent any other action. This
 * is similar to na animation. Normally it can add some offset dependent on
 * something, eg. some coordinates. Usually it gets anonymously extended in the
 * host object.
 * 
 * This class is also responsible for setting final target position, that will
 * be used for drawing.
 * 
 * @author kboom
 * 
 */
public class PositioningComponent extends Component {

	private final FixedSizeArray<PositioningOperation> operations = new FixedSizeArray<PositioningOperation>(
			5);
	
	private RunningCondition conditions;

	public PositioningComponent(RunningCondition condition) {
		this.conditions = condition;
	}

	/**
	 * Order does matter in case of them performing operations on same vector.
	 * 
	 * @param op
	 */
	public void addOperation(PositioningOperation op) {
		operations.add(op);
	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		GameObject host = (GameObject) parent;
		if(!conditions.check(host)) return;
		
		applyOperations();
		
	}

	public void applyOperations() {
		for (PositioningOperation op : operations)
			op.perform();
	}

	@Override
	public void reset() {
		
	}

	public static PositioningComponent getNeutral() {
		return new PositioningComponent(new RunningCondition() {

			@Override
			public boolean check(GameObject host) {
				return true;
			}
			
		});
	}

}
