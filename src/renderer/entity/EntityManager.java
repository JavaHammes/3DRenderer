
package renderer.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import renderer.entity.builder.BasicEntityBuilder;
import renderer.entity.builder.ComplexEntityBuilder;
import renderer.input.ClickType;
import renderer.input.Keyboard;
import renderer.input.Mouse;
import renderer.input.UserInput;
import renderer.point.MyPoint;
import renderer.point.MyVector;
import renderer.point.PointConverter;
import renderer.world.Camera;

public class EntityManager {

	private List<IEntity> entities;
	private int initialX, initialY;
	private double mouseSens = 2.5;
	private double moveSpeed = 10;
	private MyVector lightVector = MyVector.normalize(new MyVector(1, 1, 1));
	private Mouse mouse;
	private Keyboard keyboard;
	private Camera camera;

	public EntityManager() {
		this.entities = new ArrayList<IEntity>();
		this.camera = new Camera(0, 0, 0);
	}

	public void init(UserInput userInput) {
		this.mouse = userInput.mouse;
		this.keyboard = userInput.keyboard;
		this.entities.add(BasicEntityBuilder.createCube(Color.BLUE, 100, 0, -1000, 0));
		this.entities.add(BasicEntityBuilder.createDiamond(Color.RED, 100, 0, -2000, 0));
		this.entities.add(ComplexEntityBuilder.createRubiksCube(100, 0, 0, 0));
		this.entities.add(BasicEntityBuilder.createSphere(Color.RED, 100, 10, 0, 1000, 0));
		this.entities.add(BasicEntityBuilder.createPyramid(Color.YELLOW, 200, 0, 3000, 0));
		this.entities.add(BasicEntityBuilder.createCylinder(Color.GREEN, 200, 0, 2000, 0));
		this.setLighting();
		this.sortEntities();

	}

	public void update() {

		int x = this.mouse.getX();
		int y = this.mouse.getY();
		if (this.mouse.getButton() == ClickType.LeftClick) {
			int xDif = x - initialX;
			int yDif = y - initialY;
			this.rotate(true, 0, -yDif / mouseSens, -xDif / mouseSens);
		} else if (this.mouse.getButton() == ClickType.RightClick) {
			int xDif = x - initialX;

			this.rotate(true, -xDif / mouseSens, 0, 0);
		}

		if (this.mouse.isScrollingUp()) {
			PointConverter.zoomIn();
		} else if (this.mouse.isScrollingDown()) {
			PointConverter.zoomOut();
		}

		if (this.keyboard.getLeft()) {
			this.camera.translate(0, -moveSpeed, 0);
			for (IEntity entity : this.entities) {
				entity.translate(0, moveSpeed, 0);
			}
		}

		if (this.keyboard.getRight()) {
			this.camera.translate(0, moveSpeed, 0);
			for (IEntity entity : this.entities) {
				entity.translate(0, -moveSpeed, 0);
			}
		}

		if (this.keyboard.getUp()) {
			this.camera.translate(0, 0, moveSpeed);
			for (IEntity entity : this.entities) {
				entity.translate(0, 0, -moveSpeed);
			}
		}

		if (this.keyboard.getDown()) {
			this.camera.translate(0, 0, -moveSpeed);
			for (IEntity entity : this.entities) {
				entity.translate(0, 0, moveSpeed);
			}
		}

		if (this.keyboard.getForward()) {
			this.camera.translate(-moveSpeed, 0, 0);
			for (IEntity entity : this.entities) {
				entity.translate(moveSpeed, 0, 0);
			}
		}

		if (this.keyboard.getBackward()) {
			this.camera.translate(moveSpeed, 0, 0);
			for (IEntity entity : this.entities) {
				entity.translate(-moveSpeed, 0, 0);
			}
		}

		this.mouse.resetScroll();
		this.keyboard.update();

		initialX = x;
		initialY = y;
	}

	public void render(Graphics g) {
		for (IEntity entity : this.entities) {
			entity.render(g);
		}
		this.sortEntities();
	}

	public void rotate(boolean cw, double xDegrees, double yDegrees, double zDegrees) {
		for (IEntity entity : this.entities) {
			entity.rotate(cw, xDegrees, yDegrees, zDegrees, this.lightVector);
		}

		this.sortEntities();
	}

	private void setLighting() {
		for (IEntity entity : this.entities) {
			entity.setLighting(this.lightVector);
		}
	}

	public List<IEntity> sortEntities() {

		Collections.sort(this.entities, new Comparator<IEntity>() {
			@Override
			public int compare(IEntity e1, IEntity e2) {
				MyPoint p1Average = e1.getAveragePoint();
				MyPoint p2Average = e2.getAveragePoint();
				double p1Dist = MyPoint.dist(p1Average, MyPoint.ORIGIN);
				double p2Dist = MyPoint.dist(p2Average, MyPoint.ORIGIN);
				double diff = p1Dist - p2Dist;
				if (diff == 0) {
					return 0;
				}
				return diff < 0 ? 1 : -1;
			}
		});
		return this.entities;
	}

}
