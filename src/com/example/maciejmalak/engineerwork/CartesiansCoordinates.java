package com.example.maciejmalak.engineerwork;

public class CartesiansCoordinates {
	private double x,y,z;
	
	public CartesiansCoordinates(double x, double y, double z){
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
