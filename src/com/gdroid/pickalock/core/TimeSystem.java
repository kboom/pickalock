package com.gdroid.pickalock.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gdroid.pickalock.pooling.TObjectPool;
import com.gdroid.pickalock.utils.SLog;

public class TimeSystem extends BaseObject {

	private static final int did = SLog.register(TimeSystem.class);
	static {
		SLog.setTag(did, "Time system.");
	}
	
	private final Pool counterPool;
	private final HashMap<Integer, Counter> activeCounters;

	public TimeSystem() {
		counterPool = new Pool(10);
		activeCounters = new HashMap<Integer, Counter>();
	}

	@Override
	public void reset() {
		unscheduleAll();
	}
	
	protected Counter obtainCounter(Task task, long time) {
		Counter c = counterPool.allocate();
		c.task = task;
		c.looped = true;
		c.executionDelay = time;
		return c;
	}
	
	protected int obtainKey() {
		int key = 0;
		while(true) {
			if(activeCounters.containsKey(key)) key++;
			else break;
		}
		return key;
	}

	public int scheduleOnce(Task t, long delay) {
		Counter c = obtainCounter(t, delay);
		c.looped = false;	
		int key = obtainKey();
		activeCounters.put(key, c);
		return key;
	}
	
	public int scheduleAtFixedRate(Task t, long delay) {
		Counter c = obtainCounter(t, delay);
		c.looped = true;
		int key = obtainKey();
		activeCounters.put(key, c);
		return key;
	}
	
	public void unschedule(int id) {
		Counter c = activeCounters.get(id);
		c.stop();
		activeCounters.remove(id);
		counterPool.release(c);
	}
	

	public void unscheduleAll() {
		for (Integer key : activeCounters.keySet()) {
		    unschedule(key);
		}
	}

	public void startScheduled(int id) {
		activeCounters.get(id).start();
	}

	public void stopScheduled(int id) {
		Counter c = activeCounters.get(id);
		c.stop();
		c.reset();
	}

	public interface Task {
		public void execute();
	}

	/**
	 * Timertask could not be pooled.
	 * 
	 * @author kboom
	 * 
	 */
	public static class Counter implements Runnable {
		private final Thread thread = new Thread(this);
		private long executionTime = -1;
		private long executionDelay = -1;
		private volatile boolean running = false;
		private boolean looped = false;
		
		private Task task;

		public void start() {
			executionTime = System.currentTimeMillis() + executionDelay;
			running = true;
			thread.start();
		}

		public void stop() {
			running = false;
		}

		@Override
		public void run() {

			if (looped) {
				while (running) {
					try {
						Thread.sleep(executionDelay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					task.execute();
				}
			} else {
				while (running && System.currentTimeMillis() < executionTime) {
					task.execute();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

		public void reset() {
			executionTime = -1;
			executionDelay = -1;
		}

	}

	protected static class Pool extends TObjectPool<Counter> {
		public Pool(int size) {
			super(size);
			SLog.d(did, "Counter pool created.");
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {

				getAvailable().add(new Counter() {
					@Override
					public void finalize() {
						throw new IllegalStateException(
								"Finalizer run on pooled object!");
					}

				});

			}
		}

		@Override
		protected void clean(Counter t) {
			t.reset();
		}

	}


}
