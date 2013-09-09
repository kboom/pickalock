package com.gdroid.pickalock.curve;

import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;


public class LockCurveDecorator extends CurveDecorator {
	
	private LockCurveDecorator() {
		
	}
	
	public static CurveDecorator prototype() {
		return new LockCurveDecorator();
	}
	
	public LockCurveDecorator(MCurve curve) {
		super(curve);
	}
	


}
