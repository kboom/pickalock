package com.gdroid.pickalock.drawing;

import javax.microedition.khronos.opengles.GL10;

import com.gdroid.pickalock.utils.AllocationGuard;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class TextureMapping extends AllocationGuard {
	
	// android's mapping
    public int resource;
    // opengl's mapping
    public int name;
    public int width;
    public int height;
    public boolean loaded;
    
    public TextureMapping() {
        super();
        reset();
    }
    
    public void reset() {
        resource = -1;
        name = -1;
        width = 0;
        height = 0;
        loaded = false;
    }
    
}


