package org.example;

import org.example.shader.HighSpeedDistanceCalculation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class Particle {
	public ParticleType type;
	public Vector2d position = new Vector2d(0,0);
	private Vector2d velocity = new Vector2d(0,0);
	public int index;
	public Particle(int index, double x, double y, ParticleType type, World world){
		this.index = index;
		this.type = type;
		this.position =  new Vector2d(x,y);
		this.velocity = new Vector2d(0,0);
		if(ourAffections == null){
			ourAffections = world.getAffectionArray()[type.index];
		}
	}

	private ParticleAffection[] ourAffections = null;
	public Vector2d calculateForces(World world) {
		Vector2d acceleration = new Vector2d(0, 0);
		int particleCount = world.particles.length;
		double error = 0.0;
		acceleration = new Vector2d(HighSpeedDistanceCalculation.getInstance().forceX[index], HighSpeedDistanceCalculation.getInstance().forceY[index]);
//		for(int i = 0; i<particleCount; i++){
//			Particle particle = world.particles[i];
//			if(particle == this ) continue;
//			ParticleAffection affection = ourAffections[particle.type.index];
//			float x = HighSpeedDistanceCalculation.getInstance().forceX[(index) * (particleCount) + (particle.index)];
//			float y = HighSpeedDistanceCalculation.getInstance().forceY[(index) * (particleCount) + (particle.index)];
//			float distance = nn;
//			double distance_sq = particle.position.distance_sq(this.position);
//			System.out.println("--- " + new DecimalFormat("0.0000000").format(((distance * distance) - distance_sq)) + " " + Math.sqrt(distance_sq) + " " + (distance));
//			error += (Math.sqrt(distance_sq) - distance) / distance;
//			double maxDist = affection.maximumDistance;
//			maxDist *= maxDist;
//			if(distance > maxDist){
//				continue;
//			}
//			System.out.println(x + ", " + y);
//			acceleration.add(new Vector2d(x, y));
//		}
		if(index == 0){
//			System.out.println(acceleration.getX() + ", " + acceleration.getY() + ", acc");
		}
		return acceleration;
	}

	public void tick(World world){
		Vector2d acceleration = null;
		acceleration = calculateForces(world);
		this.velocity.add(acceleration);
		this.position.add(this.velocity);
		this.velocity.multiply(1.0 - WorldConstant.DRAG, 1.0 - WorldConstant.DRAG);
	}

}
