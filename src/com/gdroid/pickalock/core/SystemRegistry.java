package com.gdroid.pickalock.core;

import com.gdroid.pickalock.core.positioning.VectorFactory;
import com.gdroid.pickalock.drawing.BufferLibrary;
import com.gdroid.pickalock.drawing.RenderScheduler;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;


/**
 * A system registry that can be seen as game type itself. It contains whole
 * gameplay logic. It is heavily used by every component in the game, so one
 * change here makes everything to run differently too.
 * 
 * Subclasses sets up everything as all systems are built with strategy / state
 * pattern.
 * 
 * @author kboom
 * 
 */
public abstract class SystemRegistry extends BaseObject {

	private static final int did = SLog.register(SystemRegistry.class);
	static {
		SLog.setTag(did, "System Registry.");
		SLog.setLevel(did, Level.VERBOSE);
	}
	
	// systems do not reference each other
	// if they must interact it is done via anonymous linking
	public final VectorFactory vector;
	public final CameraSystem camera;
	public final MotionSystem motion;
	public final EventSystem event;
	public final LockMechanismSystem lockLogic;
	// internal instance, another instance is used to external clock services
	public final TimeSystem time;
	public final RenderScheduler render;
	

	public SystemRegistry() {
		vector = new VectorFactory();
		vector.setAxesOrientation(VectorFactory.AxesOrientation.LEFT_BOTTOM_FRONT);
		// push itself to BaseObject so systems can use them
		BaseObject.system = this;

		lockLogic = new LockMechanismSystem() {
			// anonymously link to the event system
			@Override
			protected void onSuccess() {
				event.trigger(EventCodes.COMPLETED, null);				
			}
			
		};
		// onTimeEnd() ...
		camera = new CameraSystem();
		time = new TimeSystem();
		event = new EventSystem();
		motion = new MotionSystem();
		render = new RenderScheduler();
		prepare();
	}
	
	@Override
	public final void update(float timeDelta, BaseObject parent) {
		time.update(timeDelta, this);
		event.update(timeDelta, this);
		motion.update(timeDelta, this);
		lockLogic.update(timeDelta, this);
	}

	// nie wchodzi
	
	@Override
	public final void reset() {
		SLog.d(did, "Reseting.");
		time.reset();
		event.reset();
		motion.reset();
		lockLogic.reset();
	}

	/**
	 * The entry point for a subclass to inject all logic. Systems are not to be
	 * subclassed. They can be altered by composition.
	 */
	protected abstract void prepare();

}
