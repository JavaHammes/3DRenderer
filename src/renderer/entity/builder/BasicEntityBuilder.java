package renderer.entity.builder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import renderer.entity.Entity;
import renderer.entity.IEntity;
import renderer.point.MyPoint;
import renderer.shapes.MyPolygon;
import renderer.shapes.Polyhedron;

public class BasicEntityBuilder {

	public static IEntity createCube(Color c, double size, double centerX, double centerY, double centerZ) {
		MyPoint p1 = new MyPoint(centerX + size / 2, centerY + -size / 2, centerZ + -size / 2);
		MyPoint p2 = new MyPoint(centerX + size / 2, centerY + size / 2, centerZ + -size / 2);
		MyPoint p3 = new MyPoint(centerX + size / 2, centerY + size / 2, centerZ + size / 2);
		MyPoint p4 = new MyPoint(centerX + size / 2, centerY + -size / 2, centerZ + size / 2);
		MyPoint p5 = new MyPoint(centerX + -size / 2, centerY + -size / 2, centerZ + -size / 2);
		MyPoint p6 = new MyPoint(centerX + -size / 2, centerY + size / 2, centerZ + -size / 2);
		MyPoint p7 = new MyPoint(centerX + -size / 2, centerY + size / 2, centerZ + size / 2);
		MyPoint p8 = new MyPoint(centerX + -size / 2, centerY + -size / 2, centerZ + size / 2);

		Polyhedron poly = new Polyhedron(new MyPolygon(c, p5, p6, p7, p8), new MyPolygon(c, p1, p2, p6, p5), new MyPolygon(c, p1, p5, p8, p4), new MyPolygon(c, p2, p6, p7, p3), new MyPolygon(c, p4, p3, p7, p8), new MyPolygon(c, p1, p2, p3, p4));

		List<Polyhedron> polys = new ArrayList<Polyhedron>();
		polys.add(poly);

		return new Entity(polys);
	}

	public static IEntity createPyramid(Color c, double size, double centerX, double centerY, double centerZ) {

		MyPoint top = new MyPoint(centerX, centerY, centerZ + size / 2);
		MyPoint p1 = new MyPoint(centerX - size / 2, centerY - size / 2, centerZ - size / 2);
		MyPoint p2 = new MyPoint(centerX, centerY + size / 2, centerZ - size / 2);
		MyPoint p3 = new MyPoint(centerX + size / 2, centerY - size / 2, centerZ - size / 2);

		Polyhedron poly = new Polyhedron(new MyPolygon(c, p1, p2, p3), new MyPolygon(c, p1, top, p3), new MyPolygon(c, p3, top, p2), new MyPolygon(c, p2, top, p1));

		List<Polyhedron> polys = new ArrayList<Polyhedron>();
		polys.add(poly);

		return new Entity(polys);

	}

	public static IEntity createCylinder(Color c, double size, double centerX, double centerY, double centerZ) {
		List<Polyhedron> polygons = new ArrayList<Polyhedron>();

		int edges = 100;
		MyPoint[] bottomCircle = new MyPoint[edges];
		MyPoint[] topCircle = new MyPoint[edges];

		for (int i = 0; i < edges; i++) {
			double theta = 2 * Math.PI / edges * i;
			double xPos = -Math.sin(theta) * size / 2;
			double yPos = Math.cos(theta) * size / 2;
			double zPosTop = +size / 2;
			double zPosButtom = -size / 2;

			topCircle[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPosTop);
			bottomCircle[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPosButtom);
		}

		MyPolygon polys[] = new MyPolygon[edges + 2];
		for (int i = 0; i < edges; i++) {
			polys[i] = new MyPolygon(topCircle[i], topCircle[(i + 1) % edges], bottomCircle[(i + 1) % edges], bottomCircle[i]);
		}

		for (int i = 0; i < bottomCircle.length / 2; i++) {
			MyPoint temp = bottomCircle[i];
			bottomCircle[i] = bottomCircle[bottomCircle.length - 1 - i];
			bottomCircle[bottomCircle.length - 1 - i] = temp;
		}

		for (int i = 0; i < topCircle.length / 2; i++) {
			MyPoint temp = topCircle[i];
			topCircle[i] = topCircle[topCircle.length - 1 - i];
			topCircle[topCircle.length - 1 - i] = temp;
		}

		polys[edges + 1] = new MyPolygon(bottomCircle);
		polys[edges] = new MyPolygon(topCircle);

		Polyhedron poly = new Polyhedron(c, false, polys);
		polygons.add(poly);

		return new Entity(polygons);
	}

	public static IEntity createDiamond(Color color, double size, double centerX, double centerY, double centerZ) {
		List<Polyhedron> polys = new ArrayList<Polyhedron>();

		int edges = 10;
		double inFactor = 0.8;
		MyPoint bottom = new MyPoint(centerX, centerY, centerZ - size / 2);
		MyPoint[] outerPoints = new MyPoint[edges];
		MyPoint[] innerPoints = new MyPoint[edges];

		for (int i = 0; i < edges; i++) {
			double theta = 2 * Math.PI / edges * i;
			double xPos = -Math.sin(theta) * size / 2;
			double yPos = Math.cos(theta) * size / 2;
			double zPos = size / 2;
			outerPoints[i] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPos * inFactor);
			innerPoints[i] = new MyPoint(centerX + xPos * inFactor, centerY + yPos * inFactor, centerZ + zPos);
		}

		MyPolygon polygons[] = new MyPolygon[2 * edges + 1];
		for (int i = 0; i < edges; i++) {
			polygons[i] = new MyPolygon(outerPoints[i], bottom, outerPoints[(i + 1) % edges]);
		}

		for (int i = 0; i < edges; i++) {
			polygons[i + edges] = new MyPolygon(outerPoints[i], outerPoints[(i + 1) % edges], innerPoints[(i + 1) % edges], innerPoints[i]);
		}
		polygons[edges * 2] = new MyPolygon(innerPoints);

		Polyhedron poly = new Polyhedron(color, false, polygons);
		polys.add(poly);

		return new Entity(polys);
	}

	public static IEntity createSphere(Color color, double size, int res, double centerX, double centerY, double centerZ) {
		List<Polyhedron> polyhedrons = new ArrayList<Polyhedron>();
		List<MyPolygon> polygons = new ArrayList<MyPolygon>();

		MyPoint bottom = new MyPoint(centerX, centerY, centerZ - size / 2);
		MyPoint top = new MyPoint(centerX, centerY, centerZ + size / 2);

		MyPoint[][] points = new MyPoint[res - 1][res];

		for (int i = 1; i < res; i++) {
			double theta = Math.PI / res * i;
			double zPos = -Math.cos(theta) * size / 2;
			double currentRadius = Math.abs(Math.sin(theta) * size / 2);
			for (int j = 0; j < res; j++) {
				double alpha = 2 * Math.PI / res * j;
				double xPos = -Math.sin(alpha) * currentRadius;
				double yPos = Math.cos(alpha) * currentRadius;
				points[i - 1][j] = new MyPoint(centerX + xPos, centerY + yPos, centerZ + zPos);
			}
		}

		for (int i = 1; i <= res; i++) {
			for (int j = 0; j < res; j++) {
				if (i == 1) {
					polygons.add(new MyPolygon(points[i - 1][j], points[i - 1][(j + 1) % res], bottom));
				} else if (i == res) {
					polygons.add(new MyPolygon(points[i - 2][(j + 1) % res], points[i - 2][j], top));
				} else {
					polygons.add(new MyPolygon(points[i - 1][j], points[i - 1][(j + 1) % res], points[i - 2][(j + 1) % res], points[i - 2][j]));
				}

			}
		}

		MyPolygon[] polygonArray = new MyPolygon[polygons.size()];
		polygonArray = polygons.toArray(polygonArray);

		Polyhedron poly = new Polyhedron(color, false, polygonArray);
		polyhedrons.add(poly);

		return new Entity(polyhedrons);
	}
}
