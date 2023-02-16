package net.andrewcpu.particle.world;


import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.constants.WorldConstant;

public class ParticleAffection {
	private ParticleType affection;
	public double strength;
	public double minimumDistance;
	public double maximumDistance;

	public static ParticleAffection getRandom(ParticleType type){
		double attractionStrength = Math.random();
		return new ParticleAffection(type, (Math.random() < 0.5 ? -1.00 : 1.00) * attractionStrength); // -1, 1
	}

	public ParticleAffection(ParticleType affection, double strength) {
		this.affection = affection;
		this.strength = strength;
		this.minimumDistance = 10;
		this.maximumDistance = 80;
		this.minimumDistance = Math.random() * WorldConstant.MIN_DIST_MULTIPLIER + WorldConstant.MINIMUM_DISTANCE;
		this.maximumDistance = (Math.random() * (WorldConstant.MAXIMUM_DISTANCE - minimumDistance)) +minimumDistance;
	}
}
