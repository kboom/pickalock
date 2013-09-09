package com.gdroid.pickalock.core.positioning;

/**
 * Each class have references to at least two vectors - target position which
 * will be modified, and source position which will be used to compute it.
 * 
 * If operation must be performed sequentially, target vector should be used as
 * source vector too. If operation should be performed independently from
 * others, the class should be set on real source.
 * 
 * This is to modify vectors.
 * 
 * @author kboom
 * 
 */
public abstract class PositioningOperation {

	// improve performance, there won't be any variations in getter

	/**
	 * A vector that is an absolute one. Usually its a combination of multiple
	 * vectors.
	 */
	protected final Vector target;

	/**
	 * A difference vector relative to target one. Operations are being
	 * performed on target one using this vector.
	 */
	protected final Vector source;

	PositioningOperation(Vector target, Vector source) {
		this.target = target;
		this.source = source;
	}

	public abstract void perform();

}
