package com.gdroid.pickalock.drawing;

// było zmieniane, może źle działać, por. z poprzednią kopią.

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.renderscript.Matrix4f;

import com.gdroid.pickalock.curve.CurveFactory;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/*
 * 
 * 
 * 
 * 
 * Dobra dobra choćby w celach edukacyjnych (nieprędko to wyjdzie z użycia, a idea pozostaje uniwersalna)
 * zostaję na razie przy open gl 1/1.1,
 * bo przejście na 2.0 wymagałoby zmiany dokładnie wszystkiego. Następna gra
 * będzie pisana w 2.0 przy czym najpierw opanuję sobie render przy okazji
 * 
 * http://www.learnopengles.com/android-lesson-one-getting-started/
 * 
 * co nie powinno być trudne dysponując tym co mam (przez analogię)
 * 
 * 
 * Aby mieć część gotowców to wczytać sobie plik OPENGL 2.0 z kopii. Tam będą też
 * komenatrze.
 * 
 * 
 */

/**
 * 
 * 
 * 
 * 
 * Motion observer musi być świadomy gdzie znajduje się kamera! Inaczej
 * współrzędne nic nie znaczą, bo nie wiadomo gdzie jesteśmy w przestrzeni.
 * 
 * 
 * 
 * 
 * 
 * 
 * http://stackoverflow.com/questions/2093096/implementing-ray-picking
 * http://ovcharov.me/2011/01/14/android-opengl-es-ray-picking/
 * 
 * Może macierze powinienem przetrzymywać jako zwykle floaty i je przestawiać po
 * prostu sobie?
 * http://www.learnopengles.com/android-lesson-one-getting-started/ a na koniec
 * podawać do opengl
 * 
 * 
 * Faktycznie, samemu powinno się trzymać te macierze!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!! When working with opengles 2 in Android you keep
 * track of your matrices yourself, usually as a couple of float[]. If you
 * calculate them directly in the shader, I don't think that you can get them.
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Opengl 2.0 nie ma już nawet funkcji translate czy scale, trzeba robić to
 * samemu! A więc każdy obiekt będzie pewnie musiał mieć dostęp jakoś do kamery
 * i zlecać przestawienia modelview...
 * 
 * dobre
 * http://stackoverflow.com/questions/6261867/question-about-3d-picking-in-
 * android-with-opengl-es-2
 * 
 * Wszelkie glulookat itd przestają mieć rację bytu, wszystkie operacje na
 * macierzach robimy systemowo a dostarczamy je po prostu do renderowania
 * 
 * Znikają funkcje jak gl.matrixMode itd.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * Na razie oldschoolowo PICK RAY (przez wczytywanie macierzy z opengl na siłę
 * przy użyciu glGetFloat - które oczywiście znika razem z translate itp itd. w
 * następnej wersji opengl). Na razie mogę sobie korzystać ze wszystkich
 * GLU.cośtam Pojawia się pytanie najważniejsze - czy przy obliczaniu ray musimy
 * znać aktaulną dla danego obiektu macierz model?
 * 
 * Jeśli tak by było to każdy renderer musi sobie trzymać kopię modelview
 * matrix. Projection się nie zmienia więc może siedzieć tutaj.
 * 
 * 
 * 
 * @author kboom
 * 
 */
public class GLCamera implements Camera {

	private static final int did = SLog.register(GLCamera.class);
	static {
		SLog.setTag(did, "Camera.");
		SLog.setLevel(did, Level.VERBOSE);
	}

	private boolean hasChanged = true;
	// Position the eye behind the origin.
	private float xEye = 0f;
	private float yEye = 0f; // put somewhere here to get pretty much good view
								// over things
	private float zEye = 3f;

	// We are looking toward the distance
	private float xLook = 0f;
	private float yLook = 0f;
	private float zLook = 0f;

	// This is where our head (not eyes!) would be pointing were we holding the
	// camera.
	final float xUp = 0.0f;
	final float yUp = 1.0f;
	final float zUp = 0.0f;

	private float near = 1f;
	private float far = 100f;

	/*
	 * 
	 * Most important matrices. (chyba do wywalenia...)
	 */

	/**
	 * Screen dimensions.
	 */
	private final int[] viewport = new int[4];

	/**
	 * This matrix can be thought of as our camera. This matrix transforms world
	 * space to eye space; it positions things relative to our eye.
	 */
	private final float[] modelview = new float[16];

	/**
	 * How the objects are rendered in the space. Probably will be used by every
	 * object to draw itself onto the screen.
	 */
	private final float[] projection = new float[16];

	// dummy one
	private final float[] dummy = new float[16];

	public GLCamera() {
		SLog.v(did, "+++ constructor +++");
	}

	public void setViewport(int i, int j, int width, int height) {
		SLog.d(did, String.format("Viewport set: %d/%d", width, height));
		viewport[0] = i;
		viewport[1] = j;
		viewport[2] = width;
		viewport[3] = height;
	}

	public void reset(GL10 gl) {
		SLog.v(did, "Reseting..");
		hasChanged = true;
	}

	public enum Projection {
		PERSPECTIVE, ORTHOGRAPHIC
	}

	public void changePerspective(GL10 gl, Projection p) {
		SLog.d(did, "Changing perspective.");
		gl.glMatrixMode(GL10.GL_PROJECTION); // select projection
		gl.glLoadIdentity(); // reset
		float aspect = ((float) viewport[2] / viewport[3]);
		switch (p) {
		case PERSPECTIVE:
			SLog.d(did, "Setting perspective mode width aspect = " + aspect);
			gl.glFrustumf(-aspect, aspect, -1f, 1f, near, far);
			break;
		case ORTHOGRAPHIC:
			SLog.d(did, "Setting orthographic mode.");
			gl.glOrthof(-1, 1, -1, 1, -1f, 10.0f);
			break;
		default:
			break;
		}

		hasChanged = true;
	}

	public void focusOnObjects(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glClearColor(0f, 0f, 0f, 1.0f);
	}

	public void update(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// dopisane
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// gl.glLoadIdentity();

		// koniec
		if (hasChanged) {
			GLU.gluLookAt(gl, xEye, yEye, zEye, xLook, yLook, zLook, xUp, yUp,
					zUp);
			hasChanged = false;
		}
		// GL11 gl11 = (GL11) gl;
		// gl11.glGetFloatv(GL10.GL_MODELVIEW, modelview, 0); // wystarczyło
		// dopisać 0 i już
		// traktuje jako
		// float
		// gl11.glGetFloatv(GL10.GL_PROJECTION, projection, 0);

		// NIEPOTRZEBNE BO DZIAła i bez tego
		/*
		 * gl.glLoadIdentity(); gl.glFrustumf(-aspect, aspect, -1f, 1f, near,
		 * far); gl.glRotatef(xAngle * 10, 1f, 0, 0); gl.glRotatef(yAngle * 10,
		 * 0, 1f, 0); gl.glRotatef(zAngle, 0f, 0f, 1f); gl.glTranslatef(xPos,
		 * yPos, zPos);
		 */

	}

	@Override
	public void accomodateTo(float near, float far) {
		this.near = near;
		this.far = far;

		hasChanged = true;
	}

	@Override
	public void move(float x, float y, float z) {
		xEye = x;
		yEye = y;
		zEye = z;

		hasChanged = true;
	}

	@Override
	public void rotate(float xAngle, float yAngle, float zAngle) {
		// just rotate the coordinates
		hasChanged = true;
	}

	@Override
	public void getRay(float x, float y, float z, float[] ray) {

		/*
		 * 
		 * normalised_x = 2 * mouse_x / win_width - 1 normalised_y = 1 - 2 *
		 * mouse_y / win_height // note the y pos is inverted, so +y is at the
		 * top of the screen
		 * 
		 * unviewMat = (projectionMat * modelViewMat).inverse()
		 * 
		 * near_point = unviewMat * Vec(normalised_x, normalised_y, 0, 1)
		 * camera_pos = ray_origin = modelViewMat.inverse().col(4) ray_dir =
		 * near_point - camera_pos
		 */

		// GLU.gluUnProject(winX, winY, winZ, model, modelOffset, project,
		// projectOffset, view, viewOffset, obj, objOffset)

		GLU.gluUnProject(x, viewport[3] - y, z, modelview, 0, projection, 0,
				viewport, 0, ray, 0);

		// fix
		if (ray[3] != 0) {
			ray[0] = ray[0] / ray[3];
			ray[1] = ray[1] / ray[3];
			ray[2] = ray[2] / ray[3];
		}

		ray[0] = ray[0] - xEye;
		ray[1] = ray[1] - yEye;
		ray[2] = ray[2] - zEye;

		// 0f jako 4 ta?
	}

	@Override
	public void lookAt(float x, float y, float z) {
		xLook = x;
		yLook = y;
		zLook = z;
	}

	/*
	 * OBOWIAZUJACE PODEJSCIE normalizedPoint[0] = (x * 2 / screenW) -1;
	 * normalizedPoint[1] = 1 - (y * 2 / screenH); normalizedPoint[2] = ?
	 * normalizedPoint[3] = ? matrix = perspective_matrix x model_matrix
	 * inv_matrix = inverse(matrix) outpoint = inv_matrix x normalizedPoint
	 */

	// albo to?

	/*
	 * public void unproject(float [] in, float [] out) {
	 * 
	 * // multiply to get mvp float [] mvp = dummy; Matrix.multiplyMM(mvp, 0,
	 * projection, 0, modelview, 0); // invert it Matrix.invertM(mvp, 0, mvp,
	 * 0);
	 * 
	 * // screen coordinates are already normalized // compute it
	 * Matrix.multiplyMV(out, 0, mvp, 0, in, 0);
	 * 
	 * if (out[3] == 0) return null;
	 * 
	 * out[3] = 1 / out[3];
	 * 
	 * float o1 = out[1]; float o2 = out[2]; float o3 = out[3]; float o4 =
	 * out[4];
	 * 
	 * out[0] = o1 * o3; out[1] = o1 * o3; out[2] = o2 * o3; out[3] = 0; // not
	 * really needed...
	 * 
	 * // potem
	 * 
	 * Vec3 near = unProject(30, 50, 0, mvpMatrix, 800, 480); Vec3 far =
	 * unProject(30, 50, 1, mvpMatrix, 800, 480); // 1 for winz means projected
	 * on the far plane Vec3 pickingRay = Subtract(far, near); // Vector
	 * subtraction
	 * 
	 * }
	 */

}
