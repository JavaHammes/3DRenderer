package renderer.entity;

import java.awt.Graphics;

import renderer.point.MyPoint;
import renderer.point.MyVector;

public interface IEntity {

	void render(Graphics g);

	void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees, MyVector lightVector);

	void translate(double x, double y, double z);

	void setLighting(MyVector lightVector);

	MyPoint getAveragePoint();

}
