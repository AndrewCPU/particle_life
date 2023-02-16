package net.andrewcpu.particle.shader;

import net.andrewcpu.particle.world.Particle;
import net.andrewcpu.particle.world.World;

import java.util.List;

public class JavaShaderBridge {
	private static JavaShaderBridge instance = null;

	public static JavaShaderBridge getInstance() {
		if (instance == null) {
			instance = new JavaShaderBridge();
		}
		return instance;
	}

	public boolean initialized;
	private ShaderEngine engine;
	private PhysicsShader distanceProgram;

	private JavaShaderBridge() {
		initialized = false;
		instance = this;
	}

	public void initialize(World world) {
		this.engine = new ShaderEngine();
		engine.start();
		this.distanceProgram = new PhysicsShader();
		distanceProgram.init(world, engine);
		initialized = true;
	}

	public float[] forceX;
	public float[] forceY;

	public void getDistances(List<Particle> particles) {
		try {
			if (!initialized) {
				throw new RuntimeException("Program initialization required");
			}
			float[] dt = new float[particles.size() * 2];
			particles.stream().forEach(particle -> {
				dt[particle.index * 2] = (float) particle.position.getX();
				dt[particle.index * 2 + 1] = (float) particle.position.getY();
			});
			distanceProgram.ingestAndExecute(dt);
			forceX = distanceProgram.resultX;
			forceY = distanceProgram.resultY;

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		initialized = false;
		distanceProgram.release();
		engine.release();
	}

	public void updateAffections() {
		distanceProgram.updateAffections();
	}
}
