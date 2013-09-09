package com.gdroid.pickalock.drawing;

import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

// implementing MotionObserver allows to simulate touch
public class GLSurface extends GLSurfaceView implements MotionObserveable,
		MotionObserver {

	private static final int did = SLog.register(GLSurface.class);
	static {
		SLog.setTag(did, "GL Surface.");
		SLog.setLevel(did, Level.WARN);
	}
	
	
	private FixedSizeArray<MotionObserver> observers;

	public GLSurface(Context context, Renderer renderer) {
		super(context);
		this.setRenderer(renderer);
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
		observers = new FixedSizeArray<MotionObserver>(20);
	}

	@Override
	public boolean registerMotionObserver(MotionObserver o) {
		if (observers.find(o, true) == -1) {
			observers.add(o);
			return true;
		} else
			return false;
	}

	@Override
	public boolean unregisterMotionObserver(MotionObserver o) {
		int index = observers.find(o, true);
		if (index != -1) {
			observers.remove(index);
			return true;
		} else
			return false;
	}

	@Override
	public void onDownTouch(final int x, final int y, final int pointerId) {
		queueEvent(new Runnable() {
			public void run() {
				for (MotionObserver o : observers) {
					o.onDownTouch(x, y, pointerId);
				}
			}
		});

	}

	@Override
	public void onUpTouch(final int x, final int y, final int pointerId) {

		queueEvent(new Runnable() {
			public void run() {
				for (MotionObserver o : observers) {
					o.onUpTouch(x, y, pointerId);
				}
			}
		});

	}

	@Override
	public void onMoveTouch(final int historicalX, final int historicalY,
			final int pointerId) {
		queueEvent(new Runnable() {
			public void run() {
				for (MotionObserver o : observers) {
					o.onMoveTouch(historicalX, historicalY, pointerId);
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//dumpEvent(event);

		int p = event.getActionIndex();
		int t = event.getAction();

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			onDownTouch((int) event.getX(p), (int) event.getY(p),
					event.getPointerId(p));
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			onUpTouch((int) event.getX(p), (int) event.getY(p),
					event.getPointerId(p));
			break;
		case MotionEvent.ACTION_MOVE:
			final int historySize = event.getHistorySize();
			final int pointerCount = event.getPointerCount();


			// bez tego poniższego fora działa dobrze
			for (int h = 0; h < historySize; h++) {
				for (int p1 = 0; p1 < pointerCount; p1++) {
					onMoveTouch((int) event.getHistoricalX(p1, h),
							(int) event.getHistoricalY(p1, h),
							event.getPointerId(p1));
				}
			}
			
			for (int p1 = 0; p1 < event.getPointerCount(); p1++) {
				onMoveTouch((int) event.getX(p1), (int) event.getY(p1),
						event.getPointerId(p1));
			}
		}

		return true;
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		SLog.d(did, sb.toString());
	}

}
