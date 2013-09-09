package com.gdroid.pickalock.core;

public class LinearDurability extends DurabilityComponent {
	
	private float step;
	
	public LinearDurability() {
		step = 0.01f;
		reset();
	}
	
	public void setStep(float v) {
		step = v;
	}

	@Override
	protected void damage(float distance, float durability) {
		durability -= step;		
	}

}
