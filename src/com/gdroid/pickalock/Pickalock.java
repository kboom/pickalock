package com.gdroid.pickalock;

import android.app.Application;
import android.content.Context;

public class Pickalock extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		Pickalock.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return Pickalock.context;
	}

}
