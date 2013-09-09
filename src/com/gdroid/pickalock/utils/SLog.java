package com.gdroid.pickalock.utils;

import java.util.EnumMap;
import java.util.HashMap;

import android.util.Log;


/**
 * Provides every class with useful debugging and logging functions. Should be
 * used only in development mode. Cannot be instantiated.
 * 
 * @author kboom
 */
public class SLog {

	private final static String TAG = "System Logger";
	// holds global settings
	private static EnumMap<Parameter, Object> globalParameters;
	// holds default settings
	private static EnumMap<Parameter, Object> defaultParameters;
	// holds settings for each registered class
	private static HashMap<Integer, EnumMap<Parameter, Object>> localParameters;

	private static Boolean isActive = false;

	/**
	 * Use it to separate block where additional computations are needed to
	 * print debug message.
	 * 
	 * @return
	 */
	public static boolean isOn() {
		return isActive;
	}

	/**
	 * Use this in release version. Cannot be called twice.
	 */
	public static void turnOff() {
		Log.w(TAG, "Turning off logger.");
		isActive = false;
	}

	/**
	 * Use this in development versions. Cannot be called twice.
	 */
	public static void turnOn() {
		Log.w(TAG, "Turning on logger.");
		
		// init
		globalParameters = new EnumMap<Parameter, Object>(Parameter.class);
		defaultParameters = new EnumMap<Parameter, Object>(Parameter.class);
		localParameters = new HashMap<Integer, EnumMap<Parameter, Object>>();

		// set defaults
		defaultParameters.put(Parameter.TAG, "-");
		defaultParameters.put(Parameter.LEVEL, Level.WARN);
		isActive = true;
	}

	public static final Integer register(Class<?> c) {
		if (!isActive)
			return -1;
		Log.v(TAG, c.getName() + " is registering for logging");
		Integer id = localParameters.size();
		localParameters.put(id, new EnumMap<Parameter, Object>(Parameter.class));
		return id;
	}

	public static void setLevel(int id, Level l) {
		if (!isActive)
			return;
		setLocalParam(id, Parameter.LEVEL, l);
	}

	public static void setTag(int id, String tag) {
		if (!isActive)
			return;
		setLocalParam(id, Parameter.TAG, tag);
	}
	
	public static void crash(String msg) {
		throw new IllegalStateException(msg);
	}

	public static void i(int id, String msg) {
		if (!isActive)
			return;
		if (!checkLevel(id, Level.INFO))
			return;
		else
			Log.i(getStringParam(id, Parameter.TAG), msg);
	}

	public static void d(int id, String msg) {		
		if (!isActive)
			return;
		if (!checkLevel(id, Level.DEBUG))
			return;
		else {
			Log.d(getStringParam(id, Parameter.TAG), msg);
		}
	}

	public static void w(int id, String msg) {
		if (!isActive)
			return;
		if (!checkLevel(id, Level.WARN))
			return;
		else
			Log.w(getStringParam(id, Parameter.TAG), msg);
	}

	public static void e(int id, String msg) {
		if (!isActive)
			return;
		if (!checkLevel(id, Level.ERROR))
			return;
		else
			Log.e(getStringParam(id, Parameter.TAG), msg);
	}

	public static void e(int id, String msg, Exception ex) {
		if (!isActive)
			return;
		if (!checkLevel(id, Level.ERROR))
			return;
		else
			Log.e(getStringParam(id, Parameter.TAG),
					msg + ": " + ex.getMessage());
	}

	public static void v(int id, String msg) {
		if (!isActive)
			return;
		if (!checkLevel(id, Level.VERBOSE))
			return;
		else
			Log.v(getStringParam(id, Parameter.TAG), msg);
	}

	public static Class<?> getClassID(Object o) {
		return o.getClass();
	}

	/*
	 * PRIVATE
	 */

	private SLog() {
	}

	public static void setGlobalParam(Parameter p, Object val) {
		if (!isActive)
			return;
		if (globalParameters.containsKey(p))
			globalParameters.remove(p);
		globalParameters.put(p, val);
	}

	private static void setLocalParam(int id, Parameter p, Object val) {
		if (!isActive)
			return;
		EnumMap<Parameter, Object> params = localParameters.get(id);

		if (params.containsKey(p))
			params.remove(p);
		params.put(p, val);
	}

	private static boolean checkLevel(int id, Level l) {
		return getLevelParam(id, Parameter.LEVEL).ordinal() >= l.ordinal() ? true
				: false;
	}

	public static Boolean getBoolParam(int id, Parameter parameter) {
		return (Boolean) getParam(id, parameter);
	}

	public static String getStringParam(int id, Parameter parameter) {
		return (String) getParam(id, parameter);
	}

	public static Integer getIntParam(int id, Parameter parameter) {
		return (Integer) getParam(id, parameter);
	}

	public static Level getLevelParam(int id, Parameter parameter) {
		return (Level) getParam(id, parameter);
	}

	private static Object getParam(int id, Parameter p) {
		// check if there's such global parameter
		if (globalParameters.containsKey(p))
			return globalParameters.get(p);
		// there is no global parameter, check local
		EnumMap<Parameter, Object> params = localParameters.get(id);
		if (params != null && params.containsKey(p)) {
			return params.get(p);
		}
		// there is no local too, get default
		else
			return defaultParameters.get(p);
	}

	private static enum Parameter {
		LEVEL, TAG
	}

	public static enum Level {
		NONE, ERROR, WARN, INFO, DEBUG, VERBOSE
	}
}
