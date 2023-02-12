package org.example;

import org.example.multipliers.ForceMultiplier;
import org.example.multipliers.PointyMultiplier;
import org.example.multipliers.SquareMultiplier;

import java.util.List;

public class ParticleAffection {
	private ParticleType affection;
	public double strength;
	public double minimumDistance;
	public double maximumDistance;
	private ForceMultiplier pointyMultiplier;

	public static ParticleAffection getRandom(ParticleType type){
		double attractionStrength = 1.5 * Math.random();
		return new ParticleAffection(type, (Math.random() < 0.5 ? -1.00 : 1.00) * attractionStrength); // -1, 1
	}

	public ParticleAffection(ParticleType affection, double strength) {
		this.affection = affection;
		this.strength = strength;
		this.minimumDistance = Math.random() * WorldConstant.MIN_DIST_MULTIPLIER + WorldConstant.MINIMUM_DISTANCE;
		this.maximumDistance = (Math.random() * (WorldConstant.MAXIMUM_DISTANCE - minimumDistance)) +minimumDistance;
		this.pointyMultiplier = new PointyMultiplier();
	}

	public ParticleAffection(ParticleType affection, double strength, double minimumDistance, double maximumDistance) {
		this.affection = affection;
		this.strength = strength;
		this.minimumDistance = minimumDistance;
		this.maximumDistance = maximumDistance;
	}


	public Vector2d calculate(Particle a, Particle b, double distance){
		Vector2d vector2d = new Vector2d(0,0);
		double force = pointyMultiplier.calculate(minimumDistance,maximumDistance,distance,strength);
		vector2d.add(a.position);
		vector2d.subtract(b.position);
		vector2d.normalize();
		vector2d.multiply(force);
		return vector2d;
	}


}
