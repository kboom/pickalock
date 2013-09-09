package com.gdroid.pickalock.core;

import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.utils.SLog;

/**
 * Handles general execution of the game.
 * 
 * @author kboom
 * 
 */
public class GameThread implements Runnable {

	private static final int did = SLog.register(GameThread.class);
	static {
		SLog.setTag(did, "Game thread.");
	}

	private Thread carrier;
	private GameRoot root;
	private GameRenderer renderer;
	private long frameInterval;
	private final Object pauseLock = new Object();

	private volatile long renderTime = 0;
	private volatile long logicTime = 0;

	private volatile State state;

	public enum State {
		RUNNING, PAUSED, STOPPED
	}

	public GameThread(GameRoot root, GameRenderer renderer) {
		carrier = new Thread(this);
		this.renderer = renderer;
		this.root = root;
		setMaxFps(60f);
	}

	@Override
	public void run() {
		SLog.d(did, String.format(
				"Starting thread. Rendering target speed: %f fps", (float) 1
						/ frameInterval));

		while (state != State.STOPPED) {

			// rendering
			long ctime = System.currentTimeMillis();
			renderer.renderFrame();
			renderTime = System.currentTimeMillis() - ctime;

			// logic
			ctime = System.currentTimeMillis();
			root.update(renderTime, null);
			logicTime = System.currentTimeMillis() - ctime;

			long idletime = (long) (frameInterval - renderTime - logicTime);
			if (idletime < 0)
				SLog.w(did, "Running too slow. Delay: " + -idletime);
			else
				try {
					// sleep holds a lock if any
					Thread.sleep(idletime);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			// cycle ends, pausing stuff
			synchronized (this) {
				if (state == State.PAUSED) {
					SLog.i(did, "Game thread has been paused.");
					while (state == State.PAUSED) {
						try {
							// wait does not hold a lock
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}

	}

	public void start() {
		SLog.i(did, "Starting...");
		state = State.RUNNING;
		carrier.start();
		SLog.i(did, "Started.");
	}

	public void stop() throws InterruptedException {
		SLog.i(did, "Stopping...");
		state = State.STOPPED;
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
		carrier.join();
		SLog.i(did, "Stopped.");
	}

	/**
	 * This method ensures that a thread will not be doing anything until
	 * {@link #resume()} is called. Current working cycle will not be
	 * interrupted.
	 */
	public void pause() {
		SLog.i(did, "Pausing game thread...");
		state = State.PAUSED;
	}

	/**
	 * Makes a thread work again after {@link #pause()} had been called. Note
	 * that resuming the thread can take some time if this method was called
	 * directly after {@link #pause()}. The thread might not have been done
	 * doing current cycle and nothing can interrupt it.
	 */
	public void resume() {
		SLog.i(did, "Resuming game thread...");
		state = State.RUNNING;
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}

	public final State getState() {
		return state;
	}

	public void setMaxFps(float fps) {
		frameInterval = (long) (1 / fps * 1000);
		SLog.d(did, "Interval between frames: " + frameInterval);
	}
}
