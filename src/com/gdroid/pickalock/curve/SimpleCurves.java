package com.gdroid.pickalock.curve;

/**
 * All of this combinations are reasonable.
 * <table>
 * <tr><td>x=t</td><td>y=t</td></row>
 * <tr><td>x=t</td><td>y=t</td></row>
 * <tr><td>x=t</td><td>y=t</td></row>
 * <tr><td>x=t</td><td>y=t</td></row>
 * <tr><td>x=t</td><td>y=t</td></row>
 * <tr><td>x=t</td><td>y=t</td></row>
 * </table>
 * 
 * @author kboom
 *
 */
public class SimpleCurves extends CurveFactory.Container {
	
	
	
	public SimpleCurves() {

	}
	
	@Override
	public void open() {
		put("t","t", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return t;
			}
			
		})
		.put("t", "-t", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return -t;
			}
		})
		.put("t", "t^2", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return (float) Math.pow(t,2);
			}
		})
		.put("t", "-t^2", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return - (float) Math.pow(t,2);
			}
		})
		.put("t", "t^3", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return (float) Math.pow(t,3);
			}
		})
		.put("t", "-t^3", new MCurve() {
			@Override
			public float getValueX(float t) {
				return t;
			}
			@Override
			public float getValueY(float t) {
				return - (float) Math.pow(t,3);
			}
		});
	}
	
}
