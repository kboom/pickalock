package com.gdroid.pickalock.drawing;

import com.gdroid.pickalock.drawing.MeshShape.DrawingMode;
import com.gdroid.pickalock.utils.SLog;
import com.gdroid.pickalock.utils.SLog.Level;

/**
 * Controls shape creation. Contains some handy shortcuts for creating simple
 * shapes like cubes, circles, quads and many others. Uses general shape classes
 * that are not aware of what they are molded into. Some objects can be shared
 * among all factory users.
 * 
 * @author kboom
 * 
 */
public class ShapeFactory {

	private static final int did = SLog.register(ShapeFactory.class);
	static {
		SLog.setTag(did, "Shape factory.");
		SLog.setLevel(did, Level.DEBUG);
	}

	public enum Type {
		WEBBED, STROKED, FLOATING_MESH, FIXED_MESH
	}

	private SmartColor defColor;

	public ShapeFactory() {
		defColor = new SmartColor(1f, 1f, 1f, 1f);
	}

	public void setDefaultColor(float[] color) {
		this.setDefaultColor(new SmartColor(color[0], color[1], color[2],
				color[3]));
	}

	public void setDefaultColor(SmartColor color) {
		defColor = color;
	}

	public MeshShape createCube(float[][] colors) {

		MeshShape shape = new FloatingMeshShape(DrawingMode.INDEPENDENT, 2,
				2 * 6);

		SmartPoint pa = new SmartPoint(-1f, 1f, 1f);
		SmartPoint pb = new SmartPoint(1f, 1f, 1f);
		SmartPoint pc = new SmartPoint(-1f, -1f, 1f);
		SmartPoint pd = new SmartPoint(1f, -1f, 1f);

		SmartPoint pe = new SmartPoint(-1f, 1f, -1f);
		SmartPoint pf = new SmartPoint(1f, 1f, -1f);
		SmartPoint pg = new SmartPoint(1f, -1f, -1f);
		SmartPoint ph = new SmartPoint(-1f, -1f, -1f);

		SmartPoint[][] faces = new SmartPoint[][] { { pa, pb, pc, pd },
				{ pe, pa, ph, pc }, { pb, pf, pd, pg }, { pe, pf, pa, pb },
				{ ph, pg, pc, pd }, { pe, pf, ph, pg } };

		for (int i = 0; i < 6; i++) {
			float[] color = colors != null ? colors[i] : defColor
					.asFloatArray();

			shape.set(0, 2 * i, faces[i][0].x / 2, faces[i][0].y / 2,
					faces[i][0].z / 2, 0, 2 * i, color);
			shape.set(0, 2 * i + 1, faces[i][1].x / 2, faces[i][1].y / 2,
					faces[i][1].z / 2, 0, 2 * i + 1, color);
			shape.set(1, 2 * i, faces[i][2].x / 2, faces[i][2].y / 2,
					faces[i][2].z / 2, 1, 2 * i, color);
			shape.set(1, 2 * i + 1, faces[i][3].x / 2, faces[i][3].y / 2,
					faces[i][3].z / 2, 1, 2 * i + 1, color);
		}

		return shape;

	}

	/**
	 * Must be casted.
	 * 
	 * @param type
	 * @return
	 */
	public Shape createCustom(Type type, DrawingMode mode, int xdim, int... dim) {
		Shape shape = null;
		switch (type) {
		case WEBBED:
			shape = new WebShape(xdim);
			break;
		case FLOATING_MESH:
			shape = new FloatingMeshShape(mode, xdim, dim[0]);
			break;
		default:
			break;
		}

		return shape;
	}

	public MeshShape createGrid(SmartPoint[] points, int rows) {
		if (points.length % rows != 0)
			throw new IllegalArgumentException(
					"There must be exactly as much points to fill all the rows equally.");

		int cols = points.length / rows;
		SLog.d(did, String.format("Creating grid with dimensions %d/%d", rows,
				cols));
		MeshShape shape = new FloatingMeshShape(DrawingMode.SHARED, rows, cols);

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				SmartPoint p = points[y * cols + x];
				float[] color = p.color != null ? p.color.asFloatArray()
						: defColor.asFloatArray();
				// SLog.e(did, String.format("Filling: %f,%f,%f", p.x, p.y,
				// p.z));
				shape.set(y, x, p.x, p.y, p.z, y, x, color);
			}
		}

		return shape;
	}

	public WebShape createCircle2(float radius, int points, float[][] colors) {

		WebShape shape = new WebShape(points);
		boolean first = true;
		float fx = 0;
		float fy = 0;

		for (int i = 0; i < points; i++) {
			double fi = 2 * Math.PI * i / points;
			double xa = radius * Math.sin(fi + Math.PI);
			double ya = radius * Math.cos(fi + Math.PI);
			if (first) {
				first = false;
				fx = (float) xa;
				fy = (float) ya;
			}

			float[] color = colors != null ? colors[i] : defColor.asFloatArray();

			shape.set(i, (float) xa, (float) ya, 0f, 0f, i, color);
		}

		float[] color = colors != null ? colors[colors.length - 1] : defColor.asFloatArray();
		shape.set(points - 1, fx, fy, 0f, 0f, 0f, color);

		return shape;

	}

	public StrokeShape createStroke(SmartPoint[] coords) {
		StrokeShape shape = new StrokeShape(coords.length);

		for (int i = 0; i < coords.length; i++) {
			SmartPoint p = coords[i];
			float[] color = p.color != null ? p.color.asFloatArray() : defColor
					.asFloatArray();
			shape.set(i, p.x, p.y, p.z, 0, i, color);
		}

		return shape;
	}

	/**
	 * Assumes z = 0. Will stretch colors.
	 * 
	 * @param fullPath
	 * @return
	 */
	public StrokeShape createStroke(float[] path, float[][] colors) {
		if (path.length != colors.length)
			throw new IllegalArgumentException(
					"There must be as much colors as points!");
		
		StrokeShape shape = new StrokeShape(path.length / 2);
		shape.setStrokeWidth(5f); // do wywalenia
		for (int i = 0; i < path.length / 2; i++) {
			float[] color = colors != null ? colors[i] : defColor.asFloatArray();
			shape.set(i, path[2 * i] / 2, path[2 * i + 1] / 2, 0f, 0, 0, color);
		}

		return shape;
	}
	
	/**
	 * Assumes z = 0. Will stretch colors.
	 * 
	 * @param fullPath
	 * @return
	 */
	public StrokeShape createStroke(float[] path, SmartColor [] colors) {
		if (path.length != colors.length)
			throw new IllegalArgumentException(
					"There must be as much colors as points!");
		
		StrokeShape shape = new StrokeShape(path.length / 2);
		shape.setStrokeWidth(5f); // do wywalenia
		for (int i = 0; i < path.length / 2; i++) {
			float[] color = colors != null ? colors[i].asFloatArray() : defColor.asFloatArray();
			shape.set(i, path[2 * i] / 2, path[2 * i + 1] / 2, 0f, 0, 0, color);
		}

		return shape;
	}
	
}
