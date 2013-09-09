package com.gdroid.pickalock;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.R;
import com.gdroid.pickalock.R.menu;
import com.gdroid.pickalock.core.ContextParams;
import com.gdroid.pickalock.core.DurabilityComponent;
import com.gdroid.pickalock.core.Game;
import com.gdroid.pickalock.core.SandboxRegistry;
import com.gdroid.pickalock.core.SystemRegistry;
import com.gdroid.pickalock.drawing.GLCamera;
import com.gdroid.pickalock.drawing.GLRenderer;
import com.gdroid.pickalock.drawing.GLSurface;
import com.gdroid.pickalock.drawing.GameRenderer;
import com.gdroid.pickalock.drawing.RenderScheduler;
import com.gdroid.pickalock.utils.SLog;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

//emulator does not support open gl 2.0!!! Test on the phone.
// glSurface.setEGLContextClientVersion(2);
public class MenuActivity extends Activity {

	private static final int did = SLog.register(MenuActivity.class);
	static {
		SLog.setTag(did, "Menu activity.");
		SLog.turnOn();
	}

	private Game game = new Game();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SystemRegistry registry = new SandboxRegistry();
		GameRenderer renderer = new GLRenderer(registry.render, new GLCamera()) {
			@Override 
			public void onSurfaceChanged(GL10 gl, int width, int height) {
				super.onSurfaceChanged(gl, width, height);
				ContextParams.screenWidth = width;
				ContextParams.screenHeight = height;
				// and other things probably
				game.onContextParamsChange(); // notify the game
				
			}
		};		
		GLSurface surface = new GLSurface(this, renderer);
		renderer.setSurface(surface);	

		// should be called only once, any changes then are made
		// by calling proper functions.
		populateParams(); // screen dimensions will be obtained automatically after the game starts... 
		game.bootstrap(renderer, registry);
		game.start();
		
		setContentView(surface);
	}
	
	

	@Override
	protected void onPause() {
		game.stop();
		game.end();
		finish();
		super.onPause();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
	
	private void populateParams() {
		
		 // initialize the DisplayMetrics object
		 DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();

		 // populate the DisplayMetrics object with the display characteristics
		 getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

		 // get the width and height
		 ContextParams.screenWidth = deviceDisplayMetrics.widthPixels;
		 ContextParams.screenHeight = deviceDisplayMetrics.heightPixels;
		 
		 SLog.d(did, "Populate parameters: width = " + ContextParams.screenWidth);
	}

}
