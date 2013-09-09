package com.gdroid.pickalock.drawing;

public class SmartPoint {
	
		public final float x;
		public final float y;
		public final float z;
		public final SmartColor color;

		public SmartPoint(float x, float y, SmartColor c) {
			this(x, y, 0, c);
		}

		public SmartPoint(float x, float y) {
			this(x, y, 0, null);
		}

		public SmartPoint(float x, float y, float z) {
			this(x, y, z, null);
		}

		public SmartPoint(float x, float y, float z, SmartColor c) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.color = c;
		}

}
