package org.example;

import java.util.Vector;

public class Vector2d {
	private double x;
	private double y;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void add(Vector2d other){
		this.x += other.x;
		this.y += other.y;
	}

	public void subtract(Vector2d other)
	{
		this.x -= other.x;
		this.y -= other.y;
	}

	public void multiply(Vector2d other){
		multiply(other.x, other.y);
	}

	public void multiply(double d){
		multiply(d, d);
	}

	public void multiply(double nx, double ny){
		this.x *= nx;
		this.y *= ny;
	}

	public void divide(double nx, double ny){
		this.x /= nx;
		this.y /= ny;
	}

	public void divide(Vector2d other){
		divide(other.x, other.y);
	}

	public double length() {
		return Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
	}

	public Vector2d clone() {
		return new Vector2d(x, y);
	}

	public void normalize() {
		double length = length();
		divide(length, length);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double distance(Vector2d vector2d){
		return Math.sqrt(Math.pow(x - vector2d.x, 2) + Math.pow(y - vector2d.y, 2));
	}
	public double distance_sq(Vector2d vector2d){
		double n1 = x - vector2d.x;
		n1 *= n1;
		double n2 = y - vector2d.y;
		n2 *= n2;
		return n1 + n2;
	}

	public double[] toArray() {
		return new double[]{x, y};
	}
}
