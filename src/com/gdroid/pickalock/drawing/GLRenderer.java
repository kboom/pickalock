package com.gdroid.pickalock.drawing;

import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.drawing.GLCamera.Projection;
import com.gdroid.pickalock.utils.SLog;
import android.opengl.GLSurfaceView;

public class GLRenderer implements GameRenderer {
	
	private static final int did = SLog.register(GLRenderer.class);
	static {
		SLog.setTag(did, "GL Renderer.");
	}	
	
	private GLSurface surface;
	private RenderScheduler renderScheduler;
	private GLCamera camera;
	
	public GLRenderer(RenderScheduler scheduler, GLCamera camera) {
    	this.camera = camera;
    	this.renderScheduler = scheduler;
    }   	
	
	public void reset(GL10 gl) {

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		camera.setViewport(0, 0, width, height);		
		camera.changePerspective(gl, Projection.PERSPECTIVE);
		
		SLog.d(did, "Surface created.");
	}


	public void setCamera(GLCamera camera) {
		this.camera = camera;	
	}

	/**
	 * Makes renderer work.
	 */
	public void renderFrame() {
		surface.requestRender();	
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {	
		camera.reset(gl);		
	}

	@Override
	public void onDrawFrame(GL10 gl) {			
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
	    	  
	    camera.update(gl);
	    camera.focusOnObjects(gl);
	    
	   
        for(Renderable r : renderScheduler.getRenderList(gl)) {       
        	gl.glPushMatrix();
        	r.draw(gl);       
        	gl.glPopMatrix();
        }  
 
	}


	@Override
	public void setSurface(GLSurface surface) {
		this.surface = surface;		
	}


	@Override
	public GLSurface getSurface() {
		return surface;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

}
