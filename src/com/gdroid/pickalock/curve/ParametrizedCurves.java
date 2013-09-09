package com.gdroid.pickalock.curve;

public class ParametrizedCurves extends CurveFactory.Container {

	@Override
	public void open() {
		super.open();
		super.put("butterfly", "1", new MCurve() {
			@Override
			public float getValueX(float t) {
				if(t>1f) return -1;
				else return -(float) (0.5f * Math.cos(2*Math.PI*t))+0.5f; // minus
			}
			
			@Override
			public float getValueY(float t) {				
				return (float) (0.5f * Math.sin(4*Math.PI*t))+0.5f;
			}
		});
	}

}
