package net.andrewcpu.particle.render.panels;

import net.andrewcpu.particle.enums.ParticleType;

import javax.swing.*;

public class ParticleTypeCustomizer extends JComponent {
	private ParticleType particleType;

	public ParticleTypeCustomizer(ParticleType type, JComponent parent) {
		this.particleType = type;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(parent.getPreferredSize());
		_ParticlePickers comp = new _ParticlePickers(this::change, type);
		comp.setPreferredSize(parent.getPreferredSize());
		add(comp);
		setFocusable(true);

	}

	public void change(ParticleType type, double value) {
		WorldCanvas.world.getAffectionArray()[this.particleType.index][type.index].strength = value;
		WorldCanvas.world.updateAffections();
	}
}
