package com.gdroid.pickalock.dressing;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.core.BaseObject;
import com.gdroid.pickalock.core.Component;
import com.gdroid.pickalock.core.GameObject;
import com.gdroid.pickalock.core.GameObjectStatusCodes;
import com.gdroid.pickalock.drawing.ObjectAvatar;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * 
 * @author kboom
 * 
 */
public class Outfit extends Component implements ObjectAvatar {

	private static final int did = SLog.register(Outfit.class);
	static {
		SLog.setTag(did, "Outfit.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private ObjectAvatar currentAvatar;

	public enum State {
		NORMAL("normal"), BROKEN("broken"), BADLY_DAMAGED("badlydamaged"), DAMAGED(
				"damaged"), SQUEEZED("squeezed");

		private String name;

		State(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private final Map<State, ObjectAvatar> looks;

	private Outfit() {
		looks = new HashMap<State, ObjectAvatar>();
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		GameObject host = (GameObject) parent;
		if (host.checkFlag(GameObjectStatusCodes.GRABBED)) {
			changeMask(State.SQUEEZED);
		}
	}

	private void changeMask(State t) {
		if (looks.containsKey(t)) {
			currentAvatar = looks.get(t);
		}
		// do nothing otherwise
	}

	@Override
	public void reset() {
		currentAvatar = looks.get(State.NORMAL);
		if (currentAvatar == null)
			throw new IllegalStateException("Normal state missing.");
	}

	public static class Builder {
		private Outfit result;

		public Builder() {
			result = new Outfit();
		}

		public Builder add(State type, ObjectAvatar avatar) {
			SLog.e(did,
					"Adding: " + type.getName() + ", avatar: " + avatar.hashCode());
			if(result.looks.containsKey(type)) 
				SLog.w(did, "Overriding previous assigment.");
			result.looks.put(type, avatar);
			return this;
		}

		public Outfit build() {
			result.reset();
			return result;
		}

	}

	@Override
	public void generateHardwareBuffers(GL10 gl) {
		currentAvatar.generateHardwareBuffers(gl);
	}

	@Override
	public void invalidateHardwareBuffers() {
		currentAvatar.invalidateHardwareBuffers();
	}

	@Override
	public void releaseHardwareBuffers(GL10 gl) {
		currentAvatar.releaseHardwareBuffers(gl);
	}

	@Override
	public void beginDrawing(GL10 gl) {
		currentAvatar.beginDrawing(gl);
	}

	@Override
	public void draw(GL10 gl) {
		currentAvatar.draw(gl);
	}

	@Override
	public void endDrawing(GL10 gl) {
		currentAvatar.endDrawing(gl);
	}

}
