package net.andrewcpu.particle.util;

import net.andrewcpu.particle.enums.ParticleType;

public interface ParticleValueChangeEvent {
	void particleChange(ParticleType type, double value);
}
