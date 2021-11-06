package renderer.shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import renderer.point.MyPoint;
import renderer.point.MyVector;
import renderer.point.PointConverter;

public class MyPolygon {

	private static final double AMBIENT_LIGHT = 0.05;

	public MyPoint[] points;
	private Color baseColor, lightingColor;
	private boolean visible;

	public MyPolygon(Color c, MyPoint... points) {
		this.baseColor = this.lightingColor = c;
		this.points = new MyPoint[points.length];

		for (int i = 0; i < points.length; i++) {
			MyPoint p = points[i];
			this.points[i] = new MyPoint(p.x, p.y, p.z);
		}
		this.updateVisibility();
	}

	public MyPolygon(MyPoint... points) {
		this.baseColor = this.lightingColor = Color.WHITE;
		this.points = new MyPoint[points.length];

		for (int i = 0; i < points.length; i++) {
			MyPoint p = points[i];
			this.points[i] = new MyPoint(p.x, p.y, p.z);
		}
		this.updateVisibility();
	}

	public MyPoint[] getPoints() {
		return this.points;
	}

	public void render(Graphics g) {
		if (!this.visible) {
			return;
		}
		Polygon poly = new Polygon();
		for (int i = 0; i < this.points.length; i++) {
			Point p = PointConverter.covertPoint(points[i]);
			poly.addPoint(p.x, p.y);
		}

		g.setColor(this.lightingColor);
		g.fillPolygon(poly);
	}

	public void translate(double x, double y, double z) {
		for (MyPoint p : this.points) {
			p.xOffset += x;
			p.yOffset += y;
			p.zOffset += z;
		}
		this.updateVisibility();
	}

	public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector) {
		for (MyPoint p : this.points) {
			PointConverter.rotateAxisX(p, CW, xDegrees);
			PointConverter.rotateAxisY(p, CW, yDegrees);
			PointConverter.rotateAxisZ(p, CW, zDegrees);

		}
		this.updateVisibility();
		this.setLighting(lightVector);
	}

	public double getAverageX() {
		double sum = 0;
		for (MyPoint p : this.points) {
			sum += p.x + p.xOffset;
		}

		return sum / this.points.length;
	}

	public void setColor(Color c) {
		this.baseColor = c;
	}

	public static MyPolygon[] sortPolygons(MyPolygon[] polygons) {
		List<MyPolygon> polygonList = new ArrayList<MyPolygon>();

		for (MyPolygon poly : polygons) {
			polygonList.add(poly);
		}

		Collections.sort(polygonList, new Comparator<MyPolygon>() {
			@Override
			public int compare(MyPolygon p1, MyPolygon p2) {
				MyPoint p1Average = p1.getAveragePoint();
				MyPoint p2Average = p2.getAveragePoint();
				double p1Dist = MyPoint.dist(p1Average, MyPoint.ORIGIN);
				double p2Dist = MyPoint.dist(p2Average, MyPoint.ORIGIN);
				double diff = p1Dist - p2Dist;
				if (diff == 0) {
					return 0;
				}
				return diff < 0 ? 1 : -1;
			}
		});

		for (int i = 0; i < polygons.length; i++) {
			polygons[i] = polygonList.get(i);
		}

		return polygons;
	}

	public void setLighting(MyVector lightVector) {
		if (this.points.length < 3) {
			return;
		}
		MyVector v1 = new MyVector(this.points[0], this.points[1]);
		MyVector v2 = new MyVector(this.points[1], this.points[2]);
		MyVector normal = MyVector.normalize(MyVector.cross(v2, v1));
		double dot = MyVector.dot(normal, lightVector);
		double sign = dot < 0 ? -1 : 1;
		dot = sign * dot * dot;
		dot = (dot + 1) / 2 * (1 - AMBIENT_LIGHT);

		double lightRatio = Math.min(1, Math.max(0, AMBIENT_LIGHT + dot));
		this.updateLightingColor(lightRatio);
	}

	public boolean isVisible() {
		return this.visible;
	}

	private void updateVisibility() {
		this.visible = this.getAverageX() < 0;
	}

	private void updateLightingColor(double lightRatio) {
		int red = (int) (this.baseColor.getRed() * lightRatio);
		int green = (int) (this.baseColor.getGreen() * lightRatio);
		int blue = (int) (this.baseColor.getBlue() * lightRatio);

		this.lightingColor = new Color(red, green, blue);

	}

	public MyPoint getAveragePoint() {
		double x = 0;
		double y = 0;
		double z = 0;
		for (MyPoint p : this.points) {
			x += p.x + p.xOffset;
			y += p.y + p.yOffset;
			z += p.z + p.zOffset;
		}

		x /= this.points.length;
		y /= this.points.length;
		z /= this.points.length;

		return new MyPoint(x, y, z);
	}
}
