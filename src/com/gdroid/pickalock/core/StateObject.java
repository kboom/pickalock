package com.gdroid.pickalock.core;

import com.gdroid.pickalock.pooling.FixedSizeArray;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/*
 * 
 * Log cannot reamin in the form it is because even if the log is not displayed,
 * strings as a parameters are still created!
 * 
 * 
 * 
 */

/**
 * Every object that has some internal state should extend this class. Internal
 * state is needed for nearly every component. Is not synchronized. (it would
 * suffice to put Collections.synchronizedSet(obj) to get thread safe set.
 * 
 * !! maybe enums can contain some state patterns? Enum implementing command...
 * interesting!
 * 
 * @author kboom
 * 
 * @param <E>
 */
public class StateObject<E extends Enum<E>> extends BaseObject {

	private static final int did = SLog.register(StateObject.class);
	static {
		SLog.setTag(did, "State object.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private final Class<E> type;
	private final FixedSizeArray<E> flags;
	private final FixedSizeArray<E> pendingAdditions;
	private final FixedSizeArray<E> pendingRemovals;

	protected StateObject(Class<E> t) {
		type = t;
		int codeCount = GameObjectStatusCodes.values().length;
		flags = new FixedSizeArray<E>(codeCount);
		pendingAdditions = new FixedSizeArray<E>(codeCount);
		pendingRemovals = new FixedSizeArray<E>(codeCount);
	}

	/**
	 * Rises flag immediately so proceeding components will see it in this
	 * update cycle. Should be used only in game loop thread.
	 * 
	 * @param e
	 */
	public final void raiseFlag(E e) {
		SLog.d(did, super.getIdentifier() + ": Flag " + e.name() + " is raised without scheduling.");
		if (!flags.contains(e))
			flags.add(e);
	}

	
	/**
	 * Rises flag when {@link #commitStatusChanges()} is called that is usually
	 * on each update method beginning. This means this change will not be seen
	 * in this update cycle, but in the next. Should be used by external threads
	 * to avoid collisions or game loop thread itself to postpone execution.
	 * 
	 * It is thread safe.
	 * 
	 * @param e
	 */
	public synchronized final void scheduleFlagRaise(E e) {
		SLog.d(did, super.getIdentifier() + ": Flag " + e.name() + " scheduled for raising.");
		if (!pendingAdditions.contains(e))
			pendingAdditions.add(e);
	}

	/**
	 * Hides flag immediately so proceeding components will not see it in this
	 * update cycle. Should be used only in game loop thread.
	 * 
	 * @param e
	 */
	public final void hideFlag(E e) {
		SLog.d(did, super.getIdentifier() + ": Flag " + e.name() + " is hidden without scheduling.");
		if (flags.contains(e))
			flags.remove(e);
	}

	/**
	 * Hides flag when {@link #commitStatusChanges()} is called that is usually
	 * on each update method beginning. This means this change will not be seen
	 * in this update cycle, but in the next. Should be used by external threads
	 * to avoid collisions or game loop thread itself to postpone execution.
	 * 
	 * It is thread safe.
	 * 
	 * @param e
	 */
	public synchronized final void scheduleFlagHide(E e) {
		SLog.d(did, super.getIdentifier() + ": Flag " + e.name() + " scheduled for hiding.");
		if (!pendingRemovals.contains(e))
			pendingRemovals.add(e); // a nie addAll(EnumSet.of(..))?
	}

	public final boolean checkFlag(E e) {
		return flags.contains(e);
	}

	public synchronized final void hideAllFlags() {
		SLog.d(did, super.getIdentifier() + ": All flags scheduled for hiding.");
		pendingRemovals.clear();
		pendingRemovals.addAll(flags);
	}

	
	/**
	 * Commits all scheduled flag operations.
	 */
	public synchronized final void commitStatusChanges() {

		for (E e : pendingRemovals) {
			if (flags.contains(e)) {
				SLog.d(did, "Deactivating " + e.name() + " flag.");
				flags.remove(e);
			}
		}

		for (E e : pendingAdditions) {
			if (!flags.contains(e)) {
				SLog.d(did, "Activating " + e.name() + " flag.");
				flags.add(e);
			}
		}

		pendingAdditions.clear();
		pendingRemovals.clear();
	}

	@Override
	public void reset() {
		SLog.i(did, "Reset.");
		flags.clear();
		pendingRemovals.clear();
		pendingAdditions.clear();
	}

	@Override
	protected void update(float timeDelta, BaseObject parent) {
		super.update(timeDelta, parent);

	}

}
