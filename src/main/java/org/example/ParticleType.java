package org.example;

import java.awt.*;

public enum ParticleType {
	RED(new Color(255, 135, 135),0),
	BLUE(new Color(188, 226, 158),1),
	GREEN(new Color(160, 60, 120),2),
//	PINK(Color.PINK),
	ORANGE(new Color(245,213,174),3),
	CYAN(new Color(255,0,50),4),
	YELLOW(new Color(40, 255, 191),5);
	private final Color color;
	public final int index;

	ParticleType(Color color, int index) {
		this.color = color;this.index = index;
	}

	public Color getColor() {
		return color;
	}


}
