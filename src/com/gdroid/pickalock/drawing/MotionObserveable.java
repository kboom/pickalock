package com.gdroid.pickalock.drawing;

public interface MotionObserveable {
	boolean registerMotionObserver(MotionObserver o);
	boolean unregisterMotionObserver(MotionObserver o);
}
