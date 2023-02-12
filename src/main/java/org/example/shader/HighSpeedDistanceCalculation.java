package org.example.shader;

import org.example.Particle;
import org.example.World;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class HighSpeedDistanceCalculation {
	private static HighSpeedDistanceCalculation instance = null;
	public static HighSpeedDistanceCalculation getInstance() {
		if(instance == null){
			instance = new HighSpeedDistanceCalculation();
		}
		return instance;
	}
	public boolean initialized = false;
	private ShaderEngine engine;
	private PhysicsShaderv2 distanceProgram;
	private HighSpeedDistanceCalculation() {
		initialized = false;
		instance = this;
	}

	public void initialize(World world) {
		this.engine = new ShaderEngine();
		engine.start();
		this.distanceProgram = new PhysicsShaderv2();
		distanceProgram.init(world, engine);
		initialized = true;
	}

	public float[] forceX;
	public float[] forceY;

	public float[][] getDistances(List<Particle> particles){
		try {
			if(!initialized){
				throw new RuntimeException("Program initialization required");
			}
			float[] dt = new float[particles.size() * 2];
			particles.stream().forEach(particle -> {
				dt[particle.index * 2] = (float)particle.position.getX();
				dt[particle.index * 2+1] = (float)particle.position.getY();
			});
			 distanceProgram.ingestAndExecute(dt);
			forceX = distanceProgram.resultX;
			forceY = distanceProgram.resultY;
			 return new float[][]{forceX, forceY};
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new float[][]{forceX, forceY};
		}
	}

	public void dispose() {
		distanceProgram.release();
		engine.release();
	}
}
