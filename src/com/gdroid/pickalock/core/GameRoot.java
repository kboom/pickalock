package com.gdroid.pickalock.core;

import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.pooling.FixedSizeArray.StaticIterator;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

public class GameRoot extends BaseObject {

	private static final int did = SLog.register(GameRoot.class);
	static {
		SLog.setTag(did, "Game root.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private final FixedSizeArray<BaseObject> objects;
	private boolean interruptUpdate = false;

	public GameRoot() {
		objects = new FixedSizeArray<BaseObject>(30);
	}

	@Override
	public void reset() {
		SLog.d(did, "Reseting all registered base objects...");
		interruptUpdate = true;

		StaticIterator it = objects.iterator();
		it.reset();
		for (BaseObject o : objects) {
			o.reset();
		}
		SLog.d(did, "All registered base objects have been reset.");
	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);

		for (BaseObject o : objects) {
			o.update(timeDelta, this);

			if (interruptUpdate) {
				SLog.i(did, "Update interrupted.");
				interruptUpdate = false;
				break;
			}
		}

	}

	public void add(BaseObject obj) {
		objects.add(obj);
	}

}
