package net.andrewcpu.particle.world;

import net.andrewcpu.particle.physics.Vector2d;
import net.andrewcpu.particle.constants.WorldConstant;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.shader.JavaShaderBridge;

public class Particle {

	public ParticleType type;
	public Vector2d position;
	public Vector2d velocity;
	public int index;
	private ParticleAffection[] ourAffections = null;


	public Particle(int index, double x, double y, ParticleType type, World world){
		this.index = index;
		this.type = type;
		this.position =  new Vector2d(x,y);
		this.velocity = new Vector2d(0,0);
		if(ourAffections == null){
			ourAffections = world.getAffectionArray()[type.index];
		}
	}

	public Vector2d calculateForces() {
		float forceX = JavaShaderBridge.getInstance().forceX[index];
		float y = JavaShaderBridge.getInstance().forceY[index];
		if(Float.isNaN(forceX)){
			forceX = 0f;
		}
		if(Float.isNaN(y)){
			y = 0f;
		}
		return new Vector2d(forceX, y);
	}

	public void tick(){
		Vector2d acceleration = calculateForces();
		Vector2d avg = acceleration.clone();
		avg.multiply(0.8,0.8);
		this.velocity.add(avg);
		this.position.add(this.velocity);
		this.velocity.multiply(1.0 - WorldConstant.DRAG, 1.0 - WorldConstant.DRAG);
	}

}
