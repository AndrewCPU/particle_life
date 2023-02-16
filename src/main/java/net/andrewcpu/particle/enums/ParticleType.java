package net.andrewcpu.particle.enums;

import java.awt.*;

public enum ParticleType {
	PINK(new Color(255, 135, 135),0, (byte)0),
	GREEN(new Color(188, 226, 158),1, (byte)1),
	PURPLE(new Color(160, 60, 120),2, (byte)2),
//	PINK(Color.PINK),
	TAN(new Color(245,213,174),3, (byte)3),
	RED(new Color(255,0,50),4, (byte)4);
//	YELLOW(new Color(40, 255, 191),5, (byte)5);
	private final Color color;
	public final int index;
	public final byte byteIndex;

	ParticleType(Color color, int index, byte byteIndex) {
		this.color = color;this.index = index;
		this.byteIndex = byteIndex;
	}

	public byte getByteIndex() {
		return byteIndex;
	}

	public Color getColor() {
		return color;
	}


}
