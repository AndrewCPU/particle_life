package net.andrewcpu.particle.render.sliders;

import javax.swing.*;
import java.awt.*;

public class CustomSlider extends JSlider {
	public CustomSlider() {
		setUI(new CustomSliderUI(this));
		setFocusable(true);
		setOpaque(false);
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}
