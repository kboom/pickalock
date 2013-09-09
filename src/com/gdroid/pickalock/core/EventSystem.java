package com.gdroid.pickalock.core;

import com.gdroid.pickalock.utils.SLog.Level;

import com.gdroid.pickalock.utils.SLog;

/**
 * Wymyślić sposób komunikacji z game! Proste - jeśli nie można robić
 * anonimowych extendów, to po prostu robić przez "command-like" - wywoływana
 * jedna komenda, dlatego można to robić totalnie anonimowo
 * (.setOnClickListener(cmd)). Inaczej trzebaby subtypy, definiować komendy bo
 * np. kolejność wykonywania jakaś by była itd.
 * 
 * @author kboom
 * 
 */
public class EventSystem extends BaseObject {

	private static final int did = SLog.register(EventSystem.class);
	static {
		SLog.setTag(did, "Event system.");
		SLog.setLevel(did, Level.VERBOSE);
	}
	
	private ObjectDamageListener onDamage;
	private GameStatusListener onGameStatus;

	@Override
	public void reset() {
		// freeze everything?
	}

	public void trigger(EventCodes code, BaseObject target) {
		switch (code) {
		case GAME_STARTED:
			SLog.i(did, "Game started event.");
			if(onGameStatus != null)
				onGameStatus.onStart();
			break;		
		case ZERO_DURABILITY:
			SLog.i(did, "Zeroed durability event on object " + target.getIdentifier());
			GameObject go = (GameObject) target;
			if (onDamage != null)
				onDamage.onObjectDestroyed(go);
			break;
		case COMPLETED:
			SLog.i(did, "Level completed event.");
			if (onGameStatus != null)
				onGameStatus.onSuccess();
			break;
		case TIME_END:
			SLog.i(did, "Time end event.");
			if (onGameStatus != null)
				onGameStatus.onFailure();
			break;
		default:
			break;
		}
	}

	public void setGameStatusListener(GameStatusListener l) {
		onGameStatus = l;
	}

	public void setOnObjectDamagedListener(ObjectDamageListener l) {
		this.onDamage = l;
	}

	public boolean isRunning() {
		return true;
	}

	public abstract static class ObjectDamageListener {
		public abstract void onObjectDestroyed(GameObject object);
	}

	public interface GameStatusListener {
		public void onStart();
		public void onFailure();
		public void onSuccess();
	}

}
