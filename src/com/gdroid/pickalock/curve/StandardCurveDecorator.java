package com.gdroid.pickalock.curve;


public class StandardCurveDecorator extends CurveDecorator {

	private StandardCurveDecorator() {
		
	}
	
	public static StandardCurveDecorator prototype() {
		return new StandardCurveDecorator();
	}
	
	public StandardCurveDecorator(MCurve curve) {
		super(curve);
	}


}
