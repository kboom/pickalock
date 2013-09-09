package com.gdroid.pickalock.core;

import java.util.ArrayList;

import com.gdroid.pickalock.core.positioning.ProgressRotationPosOperation;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.curve.ErrorCurveDecorator;
import com.gdroid.pickalock.curve.LockCurveDecorator;
import com.gdroid.pickalock.curve.MCurve;
import com.gdroid.pickalock.pooling.TObjectPool;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * The system managing true game logic which is locking mechanisms. It is a
 * proxy used by LockMechanismComponents to access LockMechanisms. It provides
 * additional information, needed for Mechanisms to run yet unknown to the
 * Components.
 * 
 * Each validation check runs on each mechanisms checking if ...
 * 
 * @author kboom
 * 
 */
public abstract class LockMechanismSystem extends BaseObject {

	private static final int did = SLog.register(LockMechanismSystem.class);
	static {
		SLog.setTag(did, "Lock mechanism system.");
		SLog.setLevel(did, Level.VERBOSE);
	}


	/**
	 * Used to enable components to use mechanism without holding a reference to
	 * it.
	 * 
	 * @author kboom
	 * 
	 */
	public enum Type {
		MOVING, BLOCKING
	}

	private ErrorCurveDecorator errorWarp;
	
	private Mechanism movMech;
	private Mechanism blkMech;
	private float maxTimeDiff;
	private final ArrayList<ProgressObserver> progressObservers;	

	private final TObjectPool<Vector3> vectorPool;
	private float ethreshold = 0.5f;

	/**
	 * Creates a raw lock mechanism system instance. This instance is to be set
	 * before use.
	 */
	public LockMechanismSystem() {
		vectorPool = system.vector.new Pool(10);
		maxTimeDiff = 0.5f;
		progressObservers = new ArrayList<ProgressObserver>();
	}

	public void setErrorCurve(MCurve c) {
		ErrorCurveDecorator dec = new ErrorCurveDecorator(c);
		this.errorWarp = dec;
	}

	public void setMechanism(Type t, Mechanism m) {
		switch (t) {
		case MOVING:
			this.movMech = m;
			break;
		case BLOCKING:
			this.blkMech = m;
			break;
		}
	}

	public void setMaxTimeDifference(float t) {
		maxTimeDiff = t;
	}

	public void setErrorThreshold(float t) {
		ethreshold = t;
	}

	/**
	 * This is to translate <strong>single points only</strong> from
	 * non-symmetric math plane (0,0)-(1,1) to open gl symmetric plane
	 * (-1,-1)-(1,1).
	 * 
	 * @param t
	 * @return
	 */
	public Vector translateBack(Type t, float arg) {
		Vector ret = system.vector.create();
		Vector val = system.vector.create();
		float x, y;

		Mechanism mech = getMech(t);
		mech.algorithm.fillValue(arg, val);

		x = mech.offset.getX() - mech.size.getX() / 2 + val.getX()
				* mech.size.getX();
		y = mech.offset.getY() - mech.size.getY() / 2 + val.getY()
				* mech.size.getY();

		ret.setCartesian(x, y, 0);
		return ret;
	}

	/**
	 * This is to locate the center of the curve symmetric plane (-1,-1)-(1,1).
	 * This curve is an effect of calling {@link #getFullPath(Type) } which
	 * translates every point from math coordinate system (only positive) to
	 * drawing coordinate system (-1,-1)-(1,1) - like shapes.
	 * 
	 * 
	 * @param t
	 * @return
	 */
	public Vector getFullPathCenter(Type t) {
		Vector ret = system.vector.create();
		Mechanism mech = getMech(t);
		ret.setCartesian(mech.offset.getX(), mech.offset.getY(), mech.offset.getZ()); // Z DOPISANE
		return ret;
	}

	/**
	 * Returns full path scaled to match shape coordinate system (-1,-1)-(1,1).
	 * This means that it can be directly used as a vertex buffer array source,
	 * and the location of the resulting shape is determined by
	 * {@link #getPlaneCenter(Type t) }
	 * 
	 * @param t
	 * @return
	 */
	public float[] getFullPath(Type t) {

		int freq = 500;
		float[] result = new float[(int) Math.floor(2 * freq)];

		Mechanism mech = getMech(t);
		Vector v = vectorPool.allocate();
		for (int i = 0; i < freq; i++) {
			mech.algorithm.fillValue((float) i / freq, v);
			result[2 * i] = (v.getX() - 0.5f) * 2;
			result[2 * i + 1] = (v.getY() - 0.5f) * 2;

			// SLog.d(this, result[2 * i] + "," + result[2 * i + 1]);
		}

		vectorPool.release(v);
		return result;
	}

	private Mechanism getMech(Type t) {
		switch (t) {
		case MOVING:
			return movMech;
		case BLOCKING:
			return blkMech;
		}
		throw new IllegalStateException("Enum not found.");
	}

	public Vector getInitialPosition(Type t) {
		// get [-1,1] "global, z=0 | f(t)=0" position
		Vector res = this.translateBack(t, 0f); 
		Vector cpos = vectorPool.allocate();
		cpos.set(res);

		Mechanism mech = getMech(t);
		// translate this position back to math plane
		translatePosition(cpos, mech.offset, mech.size);	
		// store it
		mech.lastpos.set(cpos);

		res.add(0f,0f,cpos.getZ()); // !!!!!!!!!! ADDED
		
		vectorPool.release(cpos);
		return res;
	}

	/**
	 * 
	 * @param t
	 * @param v
	 *            a movement vector. Can be null
	 * @return
	 */
	public MoveResult move(Type t, final Vector v) {
				
		if (v == null)
			return getResult(t);

		switch (t) {
		case MOVING:
			move(v, movMech, blkMech);
			
			for(int i = 0; i < progressObservers.size(); i++) 
				progressObservers.get(i).onProgress(this.getProgress());
			
			break;
		case BLOCKING:
			move(v, blkMech, movMech);
			break;
		}

		return getResult(t);
	}

	public float getProgress() {
		return movMech.time;//(movMech.time + blkMech.time) / 2;
	}

	private void translatePosition(Vector p, final Vector offset,
			final Vector size) {
		SLog.d(did,
				"translating " + p.printSimple() + " by offset "
						+ offset.printSimple() + " and size "
						+ size.printSimple());

		float nx = (p.getX() - offset.getX()) / size.getX() + 0.5f;
		float ny = (p.getY() - offset.getY()) / size.getY() + 0.5f;		
		float nz = offset.getZ(); // NZ 

		// enable assertions
		assert (nx > 0 && nx < 1) : "Position wrongly translated: x = " + nx;
		assert (ny > 0 && ny < 1) : "Position wrongly translated: y = " + ny;
	
		p.setCartesian(nx, ny, nz); // NZ
	}

	private void translateSize(final Vector scale, Vector l) {
		float nx = l.getX() / scale.getX();
		float ny = l.getY() / scale.getY();
		l.setCartesian(nx, ny, 0);
	}

	public MoveResult getResult(Type t) {
		switch (t) {
		case MOVING:
			return movMech.result;
		case BLOCKING:
			return blkMech.result;
		}
		throw new IllegalStateException("Could not recognize mechanism type.");
	}

	/**
	 * 
	 * @param v
	 *            raw coordinates
	 * @param a
	 *            triggering mechanism
	 * @param p
	 *            passive mechanism
	 */
	private void move(final Vector v, Mechanism a, Mechanism p) {
		SLog.d(did, "Moving...");

		SLog.e(did, "!!!!!! Vector: " + v.print());
		
		if ((a.time - p.time) > maxTimeDiff || a.time > 1f) {
			a.result.state = MoveResult.State.REJECTED;
			return;
		} else {
			SLog.d(did, String.format("Time difference acceptable (%f)",
					(a.time - p.time)));
		}

		Vector ea = null;
		Vector se = null;

		// make safe copy
		Vector tv = vectorPool.allocate();
		tv.set(v);
		translatePosition(tv, a.offset, a.size);
		SLog.e(did, "TRANSLATED POSITION: " + tv.print());

		ea = vectorPool.allocate();
		a.algorithm.fillError(a.time, tv, ea);

		// check if it's forward move
		if (ea.getAngle2With(a.dr) < (Math.PI / 2)) {

			SLog.d(did,
					"Move direction accepted with angle: "
							+ system.vector.toDeg(ea.getAngle2With(a.dr)) + ", dr="
							+ a.dr.print() + ", e=" + ea.print());

			se = vectorPool.allocate();
			se.set(ea);
			errorWarp.warpError(a.time, se); // scale error and use it for
												// decision but keep old one

			if (!accessError(se)) {
				SLog.e(did,
						"Scaled error not acceptable for active lockpick: "
								+ se.print() + "/" + ea.print());
				a.result.state = MoveResult.State.REJECTED;

			} else {
				a.result.state = MoveResult.State.ACCEPTED;
				a.result.error.set(ea);
				a.lastpos.set(tv);
				a.result.lastpos.set(v);
				incrementTime(a);
			}
		} else {
			SLog.d(did,
					"Move rejected with angle: "
							+ system.vector.toDeg(ea.getAngle2With(a.dr)) + ", dr="
							+ a.dr.print() + ", e=" + ea.print());

			a.result.state = MoveResult.State.REJECTED;

		}

		SLog.d(did, "Moving done.");
		vectorPool.release(se);
		vectorPool.release(ea);
		vectorPool.release(tv);

	}

	/**
	 * Increments time counter which means new move was accepted by both
	 * mechanisms.
	 * 
	 * @param delta
	 */
	private void incrementTime(Mechanism p) {
		Vector e = p.result.error;
		Vector dr = p.dr;

		float angle = e.getAngle2With(dr);
		Vector t = vectorPool.allocate();
		t.set(e);
		translateSize(p.size, t);

		float dl = (float) (t.getR2() * Math.cos(angle));
		float totallen = p.algorithm.getLength();
		float timescale = p.algorithm.getTimeScale();

		float dt = (float) (dl / totallen);
		SLog.d(did,
				String.format(
						"Time incremented by %f (actual %f): dl=%f, L=%f, T=%f, |e|=%f, |e'|=%f",
						dt, p.time, dl, totallen, timescale, e.getR2(),
						t.getR2()));

		p.time += dt;
		p.filldr(t);
		vectorPool.release(t);
		
		if (movMech.time > 0.95f) {
			SLog.i(did, "Time passed. Current time: " + this.getProgress());
			p.result.state = MoveResult.State.INVALID;
			for(int i = 0; i < progressObservers.size(); i++) {
				ProgressObserver po = progressObservers.get(i);
				po.onProgress(this.getProgress());
				po.onSuccess();
			}
			
			onSuccess();
		}
	}

	/**
	 * Simple radial criterium. It can be so simple because error warp does
	 * everything that can be done to enforce some behavior.
	 * 
	 * @param e
	 *            value of an error vector
	 * @return {@code true}, if this error is acceptable, {@code false}
	 *         otherwise
	 */
	public boolean accessError(Vector e) {
		boolean res = e.getR2() < ethreshold ? true : false;
		return res;
	}

	@Override
	public void reset() {
		SLog.d(did, "Reset.");
		movMech.reset();
		blkMech.reset();
	}
	
	public void registerObserver(ProgressObserver ppo) {
		if(!progressObservers.contains(ppo))
			progressObservers.add(ppo);		
	}

	protected abstract void onSuccess();

	/**
	 * As game logic uses only one thread, it is absolutely safe to reuse one
	 * instance of this class to provide components with information. No two
	 * components will run same time.
	 * 
	 * @author kboom
	 * 
	 */
	public static class MoveResult {
		public final Vector error = system.vector.create();
		public final Vector lastpos = system.vector.create();
		private State state = State.INVALID;
		public enum State {
			ACCEPTED, REJECTED, INVALID
		}
		
		public State getState() {
			return state;
		}

		private void reset() {
			error.reset();
			state = State.INVALID;
		}
	}

	public static class Mechanism {
		private LockCurveDecorator algorithm;
		private float time;
		private final Vector dr = system.vector.create();
		private final Vector size = system.vector.create(0.5f, 1f);
		private final Vector offset = system.vector.create();
		private final Vector lastpos = system.vector.create();
		private final MoveResult result = new MoveResult();

		public Mechanism(MCurve curve) {
			time = 0f;
			this.algorithm = new LockCurveDecorator(curve);
			filldr(lastpos);
		}

		private final void filldr(Vector v) {
			algorithm.fillValue(time, v);
			float x1 = v.getX();
			float y1 = v.getY();
			algorithm.fillValue(time + 0.0001f, v);
			dr.set(v.add(-x1, -y1, 0));
		}

		public void setOffset(float x, float y, float z) {
			offset.setCartesian(x, y, z);
		}

		public void setSize(float w, float h) {
			assert (w > 0 && h > 0) : "Size cannot be less than zero!";
			size.setCartesian(w, h, 0);
		}

		private void reset() {
			time = 0f;
			filldr(lastpos);
			this.lastpos.reset();
			this.result.reset();
		}

		public void setOffset(Vector v) {
			this.setOffset(v.getX(), v.getY(), v.getZ());
		}

		public void setSize(Vector v) {
			this.setSize(v.getX(), v.getY());
		}

	}
	
	public interface ProgressObserver {
		public void onProgress(float progress);
		public void onSuccess();
		public void onFailure();
	}

	
	

}
