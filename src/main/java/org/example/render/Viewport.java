package org.example.render;

import java.awt.*;

public class Viewport {
	private Rectangle rectangle;
	//width and height of the SCREEN
	private double scale;

	public Viewport(Rectangle rectangle, double scale) {
		this.rectangle = rectangle;
		this.scale = scale;
	}

	public Viewport(int x, int y, int width, int height, double scale){
		this.rectangle = new Rectangle(x, y, width, height);
		this.scale = scale;
	}

	public int getWidth() {
		return (int)this.rectangle.getWidth();
	}

	public int getHeight() {
		return (int)(this.rectangle.getHeight());
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public double getScale() {
		return scale;
	}
}
