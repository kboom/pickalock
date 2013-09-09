package com.gdroid.pickalock.core;

import java.util.HashMap;
import java.util.Map;

import com.gdroid.pickalock.core.EventSystem.GameStatusListener;
import com.gdroid.pickalock.core.LockMechanismSystem.Type;
import com.gdroid.pickalock.core.TimeSystem.Task;
import com.gdroid.pickalock.core.positioning.AnchorVector;
import com.gdroid.pickalock.core.positioning.Vector;
import com.gdroid.pickalock.core.positioning.Vector3;
import com.gdroid.pickalock.core.positioning.VectorFactory;
import com.gdroid.pickalock.curve.CurveFactory;
import com.gdroid.pickalock.curve.MCurve;
import com.gdroid.pickalock.curve.SimpleCurves;
import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.drawing.RenderScheduler;
import com.gdroid.pickalock.drawing.Shape;
import com.gdroid.pickalock.drawing.ShapeFactory;
import com.gdroid.pickalock.drawing.SmartColor;
import com.gdroid.pickalock.drawing.TextureLibrary;
import com.gdroid.pickalock.dressing.Dresser;
import com.gdroid.pickalock.dressing.Outfit;
import com.gdroid.pickalock.dressing.Outfit.State;
import com.gdroid.pickalock.dressing.StandardWardrobe;
import com.gdroid.pickalock.dressing.WardrobeSet;
import com.gdroid.pickalock.unused.CurvePositioningSystem;
import com.gdroid.pickalock.utils.AllocationGuard;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class Game {

	private static final int did = SLog.register(Game.class);
	static {
		SLog.setTag(did, "Game.");
		SLog.setLevel(did, Level.DEBUG);
	}

	// logic, video and audio
	private GameRenderer renderer;
	private GameRoot gameRoot;
	// and theirs host
	private GameThread gameThread;

	// shortcuts to some systems
	private VectorFactory vectorSystem;
	private EventSystem eventSystem;
	private MotionSystem motionSystem;
	private CameraSystem cameraSystem;
	private TimeSystem timeSystem;
	private RenderScheduler renderScheduler;
	private LockMechanismSystem lockSystem;

	// objects that can interact
	// when an event will be triggered, it is sufficent to do simple
	// .equals(mover/unblocker) to know everything we need!!
	private Lockpick mover;
	private Lockpick unblocker;
	private Lock lock;
	private Background background;

	// needed for later use
	private GameObjectFactory goFactory;
	private Dresser goDresser;
	private TextureLibrary texLib;
	private ShapeFactory shapeFactory;

	public Game() {

	}

	public void setTextureLibrary(TextureLibrary l) {
		texLib = l;
	}

	public void setShapeFactory(ShapeFactory f) {
		shapeFactory = f;
	}

	public void setObjectFactory(GameObjectFactory f) {
		goFactory = f;
	}

	public void setDresser(Dresser<?> d) {
		goDresser = d;
	}

	/**
	 * Creates game structure. Call {@link #start() } to begin game. Neither of
	 * factory parameters are stored, so reference shall be forgotten. They are
	 * just used to create some objects.
	 * 
	 * Should be called once.
	 */
	public void bootstrap(GameRenderer renderer, SystemRegistry sRegistry) {
		SLog.v(did, "Bootstraping the game...");
		this.renderer = renderer;
		sRegistry.camera.setCamera(renderer.getCamera());

		// for now
		setObjectFactory(new SandboxObjectFactory(renderer));
		setShapeFactory(new ShapeFactory());
		setTextureLibrary(new TextureLibrary());
		WardrobeSet<StandardWardrobe> wset = new WardrobeSet<StandardWardrobe>(
				StandardWardrobe.class);
		setDresser(new Dresser<StandardWardrobe>(shapeFactory, texLib, wset));

		// create shortcuts for each system
		referenceSystems(sRegistry);

		onContextParamsChange(); // put it somewhere else

		gameRoot = new GameRoot();
		gameThread = new GameThread(gameRoot, renderer);
		gameRoot.add(sRegistry);

		// initial level (get rid of it later)
		prepareLevel();

		eventSystem.setGameStatusListener(new GameStatusListener() {

			@Override
			public void onFailure() {
				SLog.i(did, "!!!----- FAILURE ----- !!!");
				gameThread.pause();
				gameRoot.reset();
				// gameThread.resume();
			}

			@Override
			public void onSuccess() {
				SLog.e(did, "!!!!!!!!!!!!!!!!SUCCESS!!!!!!!!!!!!!!!!!!");
				timeSystem.unscheduleAll();
				// for now
				gameThread.pause();
				gameRoot.reset();
				gameThread.resume();
			}

			@Override
			public void onStart() {
				int id = timeSystem.scheduleAtFixedRate(new Task() {

					@Override
					public void execute() {
						SLog.e(did, "TIC-TAC");
					}

				}, 1000);

				// timeSystem.startScheduled(id);
			}

		});
		SLog.v(did, "Bootstraping the game finished.");
	}

	// it should probably take source of data as a parameter
	public void prepareLevel() {
		SLog.v(did, "Preparing a level...");

		AllocationGuard.guardActive = false;

		/*
		 * TEST
		 */

		/*
		 * Vector origin = vectorSystem.create(1f, 1f, 3f); Vector direction =
		 * vectorSystem.create((float) Math.sqrt(2) / 2, (float) Math.sqrt(2) /
		 * 2, 0); float length = 5f; AnchorVector anchor =
		 * vectorSystem.createAnchor(origin, direction);
		 * anchor.setLeverLength(length); SLog.e(did,
		 * "!--------------- ANCHOR -------------!"); SLog.e(did,
		 * String.format("Anchor info: %s", anchor.print())); anchor.rotate(0,
		 * 45); SLog.e(did, String.format("Anchor info: %s", anchor.print()));
		 * anchor.setCartesian(10, 10, 3); SLog.e(did,
		 * String.format("Anchor info: %s", anchor.print()));
		 * anchor.multiply(1,1,0); SLog.e(did, String.format("Anchor info: %s",
		 * anchor.print())); anchor.multiply(2,1,0); SLog.e(did,
		 * String.format("Anchor info: %s", anchor.print()));
		 */

		/*
		 * TEST ENDS
		 */

		Vector v = BaseObject.system.vector.create();

		/**
		 * 
		 * MAY THE LOCK BE IN (0,0,0) AND THINK IN TEREMS OF AN OBJECT BEING
		 * RELATIVE TO IT.
		 * 
		 */

		final float LOCK_POS_X = 0f;
		final float LOCK_POS_Y = 0f;
		final float LOCK_POS_Z = 0f;
		final float LOCK_SIZE_X = 0.5f;
		final float LOCK_SIZE_Y = 0.5f;
		final float LOCK_SIZE_Z = 0.02f;

		final float BACKGROUND_POS_X = 0f;
		final float BACKGROUND_POS_Y = 0f;
		final float BACKGROUND_POS_Z = 0f;
		final float BACKGROUND_SIZE_X = 0.5f;
		final float BACKGROUND_SIZE_Y = 0.5f;
		final float BACKGROUND_SIZE_Z = 0.5f;

		final float BCURVE_POS_X = 2f;
		final float BCURVE_POS_Y = 0f;
		final float BCURVE_POS_Z = 1f;
		final float BCURVE_SIZE_X = 2f;
		final float BCURVE_SIZE_Y = 3f;
		final float BCURVE_SIZE_Z = 0f;

		// offset added to initial position computed from the curve
		final float BLOCKPICK_POS_X = 0f;
		final float BLOCKPICK_POS_Y = 0f;
		final float BLOCKPICK_POS_Z = 0f;
		final float BLOCKPICK_SIZE_X = 2f;
		final float BLOCKPICK_SIZE_Y = 0.05f;
		final float BLOCKPICK_SIZE_Z = 0.05f;

		final float MCURVE_POS_X = -2f;
		final float MCURVE_POS_Y = 0f;
		final float MCURVE_POS_Z = 1f;
		final float MCURVE_SIZE_X = 2f;
		final float MCURVE_SIZE_Y = 3f;
		final float MCURVE_SIZE_Z = 0f;

		// offset added to initial position computed from the curve
		final float MLOCKPICK_POS_X = 0f;
		final float MLOCKPICK_POS_Y = 0f;
		final float MLOCKPICK_POS_Z = 0f;
		final float MLOCKPICK_SIZE_X = 2f;
		final float MLOCKPICK_SIZE_Y = 0.05f;
		final float MLOCKPICK_SIZE_Z = 0.05f;

		final float MARKER_XY_SIZE_X = 0.1f;
		final float MARKER_XY_SIZE_Y = 0.1f;
		final float MARKER_XY_SIZE_Z = 0.01f;

		final float MARKERA_X = -3f;
		final float MARKERA_Y = 2f;
		final float MARKERA_Z = 0f;

		final float MARKERB_X = -2f;
		final float MARKERB_Y = 1.5f;
		final float MARKERB_Z = 0f;

		final float MARKERC_X = -1f;
		final float MARKERC_Y = 1f;
		final float MARKERC_Z = 0f;

		final float MARKER_Z_SIZE_X = 0.05f;
		final float MARKER_Z_SIZE_Y = 0.05f;
		final float MARKER_Z_SIZE_Z = 0.001f;

		final float MARKERD_X = 0f;
		final float MARKERD_Y = 0f;
		final float MARKERD_Z = 0f;

		final float MARKERE_X = -0.5f;
		final float MARKERE_Y = 0f;
		final float MARKERE_Z = 0.5f;

		final float MARKERF_X = -1f;
		final float MARKERF_Y = 0f;
		final float MARKERF_Z = 1f;

		/*
		 * MARKERS
		 */

		{
			Vector3 size = vectorSystem.create(MARKER_XY_SIZE_X,
					MARKER_XY_SIZE_Y, MARKER_XY_SIZE_Z);

			// A
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERA_X,
						MARKERA_Y, MARKERA_Z));
				gameRoot.add(builder.construct());
			}

			// B
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERB_X,
						MARKERB_Y, MARKERB_Z));
				gameRoot.add(builder.construct());
			}

			// C
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERC_X,
						MARKERC_Y, MARKERC_Z));
				gameRoot.add(builder.construct());
			}

		}

		{
			Vector3 size = vectorSystem.create(MARKER_Z_SIZE_X,
					MARKER_Z_SIZE_Y, MARKER_Z_SIZE_Z);

			// D - 0 LEVEL
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERD_X,
						MARKERD_Y, MARKERD_Z));
				gameRoot.add(builder.construct());
			}

			// E - CURVES LEVEL
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERE_X,
						MARKERE_Y, MARKERE_Z));
				gameRoot.add(builder.construct());
			}

			// F - LOCK LEVEL
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.AMBIENT,
						"marker");
				AmbientObject.MyBuilder builder = goFactory
						.getAmbientObjectBuilder(outfit);
				builder.setSize(size);
				builder.setInitStickyPoint(vectorSystem.create(MARKERF_X,
						MARKERF_Y, MARKERF_Z));
				gameRoot.add(builder.construct());
			}

		}

		/*
		 * GAME OBJECTS
		 */

		// background
		{
			Background.MyBuilder builder = goFactory
					.getBackgroundBuilder(goDresser.getOutfit(
							Dresser.Type.DOORS, "simple"));
			v.set(vectorSystem.create(BACKGROUND_POS_X, BACKGROUND_POS_Y,
					BACKGROUND_POS_Z));
			builder.setInitStickyPoint(v);
			builder.setSize(vectorSystem.create(BACKGROUND_SIZE_X,
					BACKGROUND_SIZE_Y, BACKGROUND_SIZE_Z));
			background = builder.construct(); // or maybe this way?
			gameRoot.add(background);
		}

		MCurve errorCurve = CurveFactory.getInstance().createPoly(1f);

		lockSystem.setErrorCurve(errorCurve);
		lockSystem.setErrorThreshold(1f);

		// moving stuff
		{

			MCurve curve = CurveFactory.getInstance().createPoly(1f, 0f);

			// z = 0 plane
			Vector3 coffset = vectorSystem.create(MCURVE_POS_X, MCURVE_POS_Y,
					MCURVE_POS_Z);
			Vector3 csize = vectorSystem.create(MCURVE_SIZE_X, MCURVE_SIZE_Y,
					MCURVE_SIZE_Z);

			LockMechanismSystem.Mechanism mech = new LockMechanismSystem.Mechanism(
					curve);
			mech.setOffset(coffset);
			mech.setSize(csize);
			lockSystem.setMechanism(Type.MOVING, mech);

			// pull an anchor
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.ANCHOR,
						"simple");
				Anchor.MyBuilder anchorb = goFactory.getAnchorBuilder(
						Type.MOVING, outfit);
				v.setCartesian(0.2f, 0.2f, 0f);
				anchorb.setSize(v);
				anchorb.setInitStickyPoint(lockSystem
						.getInitialPosition(Type.MOVING));
				gameRoot.add(anchorb.construct());
			}

			// and corresponding lockpick
			{
				Lockpick.MyBuilder builder = goFactory.getLockpickBuilder(
						Type.MOVING,
						goDresser.getOutfit(Dresser.Type.LOCKPICK, "simple"));

				v.setCartesian(MLOCKPICK_POS_X, MLOCKPICK_POS_Y,
						MLOCKPICK_POS_Z);
				builder.setInitStickyPoint(v);
				v.set(lockSystem.getInitialPosition(Type.MOVING));
				// v.setCartesian(-1f,-1f,1f);
				builder.setInitAnchorPoint(v);
				builder.setSize(vectorSystem.create(MLOCKPICK_SIZE_X,
						MLOCKPICK_SIZE_Y, MLOCKPICK_SIZE_Z));
				mover = builder.construct();

				gameRoot.add(mover);
			}

			// and a curve
			{
				Map<State, Shape> shapes = new HashMap<State, Shape>();
				SmartColor ga = new SmartColor(1f, 0f, 0f, 1f);
				SmartColor gb = new SmartColor(0f, 0f, 1f, 1f);
				float[] path = lockSystem.getFullPath(Type.MOVING);
				Shape s1 = shapeFactory.createStroke(path,
						SmartColor.gradient(ga, gb, path.length));
				s1.useColor();
				shapes.put(State.NORMAL, s1);
				UnlockCurve.MyBuilder builder = goFactory
						.getUnlockCurveBuilder(goDresser.getOutfit(
								Dresser.Type.CURVE, shapes, "simple"));

				builder.setType(Type.MOVING);
				builder.setInitStickyPoint(lockSystem
						.getFullPathCenter(Type.MOVING));

				builder.setSize(csize);
				UnlockCurve go = builder.construct();

				gameRoot.add(go);
			}

			// and a lock itself
			{
				Lock.MyBuilder builder = goFactory.getLockBuilder(goDresser
						.getOutfit(Dresser.Type.LOCK, "simple"));
				builder.setInitStickyPoint(BaseObject.system.vector.create(
						LOCK_POS_X, LOCK_POS_Y, LOCK_POS_Z));
				builder.setSize(vectorSystem.create(LOCK_SIZE_X, LOCK_SIZE_Y,
						LOCK_SIZE_Z));
				lock = builder.construct();
				gameRoot.add(lock);
			}
		}

		// blocking stuff

		{
			// MCurve curve = CurveFactory.getInstance().createPoly(2f, 0);

			MCurve curve = new MCurve() {
				// plot x=cos(2*PI*t)-0.2 AND y= 0.5*sin(5*2*PI*t)+0.5 from 0 to
				// 1
				@Override
				public float getValueX(float t) {
					if (t > 1f)
						return -1;
					else
						return (float) (0.5f * Math.cos(2 * Math.PI * t)) + 0.5f;
				}

				@Override
				public float getValueY(float t) {

					return (float) (0.5f * Math.sin(4 * Math.PI * t)) + 0.5f;
				}

			};

			// z = 0 plane
			Vector coffset = vectorSystem.create(BCURVE_POS_X, BCURVE_POS_Y,
					BCURVE_POS_Z);
			Vector csize = vectorSystem.create(BCURVE_SIZE_X, BCURVE_SIZE_Y,
					BCURVE_SIZE_Z);

			LockMechanismSystem.Mechanism mech = new LockMechanismSystem.Mechanism(
					curve);
			mech.setOffset(coffset);
			mech.setSize(csize);
			lockSystem.setMechanism(Type.BLOCKING, mech);

			SLog.e(did, "!!!!--------------_!!!!!");
			// pull an anchor
			{
				Outfit outfit = goDresser.getOutfit(Dresser.Type.ANCHOR,
						"simple");
				Anchor.MyBuilder anchorb = goFactory.getAnchorBuilder(
						Type.BLOCKING, outfit);
				v.setCartesian(0.2f, 0.2f, 0f);
				anchorb.setSize(v);
				v.set(lockSystem
						.getInitialPosition(Type.BLOCKING));
				SLog.e(did, "--- BLOCKING ANCHOR init pos: " + v.print());
				anchorb.setInitStickyPoint(v);
				gameRoot.add(anchorb.construct());
				SLog.e(did, "END!!!!!!!");
			}

			// and corresponding lockpick
			{
				Lockpick.MyBuilder builder = goFactory.getLockpickBuilder(
						Type.BLOCKING,
						goDresser.getOutfit(Dresser.Type.LOCKPICK, "simple"));

				v.setCartesian(BLOCKPICK_SIZE_X, BLOCKPICK_SIZE_Y,
						BLOCKPICK_SIZE_Z);
				builder.setSize(v);

				v.setCartesian(BLOCKPICK_POS_X, BLOCKPICK_POS_Y,
						BLOCKPICK_POS_Z);
				builder.setInitStickyPoint(v);
				v.set(lockSystem.getInitialPosition(Type.BLOCKING));
				builder.setInitAnchorPoint(v);

				unblocker = builder.construct();

				gameRoot.add(unblocker);
			}

			// a curve
			{
				Map<State, Shape> shapes = new HashMap<State, Shape>();
				SmartColor ga = new SmartColor(1f, 0f, 0f, 1f);
				SmartColor gb = new SmartColor(0f, 0f, 1f, 1f);
				float[] path = lockSystem.getFullPath(Type.BLOCKING);
				SmartColor[] gradient = SmartColor
						.gradient(ga, gb, path.length);
				Shape s1 = shapeFactory.createStroke(path, gradient);
				s1.useColor();
				shapes.put(State.NORMAL, s1);
				UnlockCurve.MyBuilder builder = goFactory
						.getUnlockCurveBuilder(goDresser.getOutfit(
								Dresser.Type.CURVE, shapes, "simple"));

				builder.setType(Type.BLOCKING);
				builder.setInitStickyPoint(lockSystem
						.getFullPathCenter(Type.BLOCKING));

				builder.setSize(csize);

				UnlockCurve go = builder.construct();
				gameRoot.add(go);
			}
		}

		AllocationGuard.guardActive = true;

		SLog.v(did, "Level prepared.");
	}

	/**
	 * Just create shortcuts to the systems.
	 */
	private void referenceSystems(SystemRegistry f) {
		vectorSystem = f.vector;
		eventSystem = f.event;
		timeSystem = f.time;
		motionSystem = f.motion;
		cameraSystem = f.camera;
		renderScheduler = f.render;
		lockSystem = f.lockLogic;

	}

	public void start() {
		// triggering on start event starts the game
		gameThread.start();
		eventSystem.trigger(EventCodes.GAME_STARTED, null);
	}

	public void stop() {
		eventSystem.trigger(EventCodes.GAME_STOPPED, null);
		try {
			gameThread.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		gameThread.pause();
	}

	public void resume() {
		gameThread.resume();
	}

	public void onContextParamsChange() {
		SLog.i(did, "Context parameters changed.");
		// important thing is that when we move by half of a screen, we have
		// only one half left, so normalization would be to that half a screen
		// not whole screen
		vectorSystem.setScale(ContextParams.screenWidth / 2,
				ContextParams.screenHeight / 2);
		vectorSystem.setOffset(-ContextParams.screenWidth / 2,
				-ContextParams.screenHeight / 2);

		cameraSystem.dimensionsChanged();

	}

	public void end() {
		renderer = null;
		gameRoot = null;
		gameThread = null;
		vectorSystem = null;
		eventSystem = null;
		motionSystem = null;
		renderScheduler = null;
		lockSystem = null;
		cameraSystem = null;
		mover = null;
		unblocker = null;
		lock = null;
		System.gc();
	}
}
