package com.gdroid.pickalock.drawing;

public interface MotionObserver {
	void onDownTouch(final int x, final int y, final int pointerId);

	void onUpTouch(final int x, final int y, final int pointerId);

	void onMoveTouch(final int historicalX, final int historicalY, final int pointerId);
}
