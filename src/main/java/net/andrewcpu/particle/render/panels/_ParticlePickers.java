package net.andrewcpu.particle.render.panels;

import net.andrewcpu.particle.render.sliders.CustomParticleSliderPanel;
import net.andrewcpu.particle.util.ParticleValueChangeEvent;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.util.ValueChangeEvent;

import javax.swing.*;
import java.awt.*;

public class _ParticlePickers extends JPanel {
	private ParticleType ourType;

	public _ParticlePickers(ParticleValueChangeEvent changeEvent, ParticleType ourType) {
		setBackground(OptionPane.backgroundColor.brighter());
		this.ourType = ourType;
		for (ParticleType type : ParticleType.values()) {
			add(createSlider(type.name().substring(0, 1) + type.name().toLowerCase().substring(1) + " Strength", (e) -> {
				changeEvent.particleChange(type, e / 100.0);
			}, -100, 100, (int) (WorldCanvas.world.getAffectionArray()[ourType.index][type.index].strength * 100.0), type.getColor(), type));
			add(getPadding(50));
		}
		setFocusable(true);

	}

	public JComponent getPadding(int padd) {
		JComponent component = new JPanel();
		component.setPreferredSize(new Dimension(getWidth(), padd));
		component.setSize(new Dimension(getWidth(), padd));
		return component;
	}

	private JPanel createSlider(String labelText, ValueChangeEvent event, int min, int max, int value, Color color, ParticleType target) {
		CustomParticleSliderPanel sliderPanel = new CustomParticleSliderPanel(min, max, value, event, labelText, ourType, target);
		sliderPanel.setThumbColor(color);
		return sliderPanel;
	}

	private void resizeLabel(JLabel label) {
		label.setPreferredSize(new Dimension(150, 20));
		label.setSize(new Dimension(150, 20));
	}

	public void updateValues() {

	}
}
