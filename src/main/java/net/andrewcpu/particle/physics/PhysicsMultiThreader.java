package net.andrewcpu.particle.physics;

import net.andrewcpu.particle.shader.JavaShaderBridge;
import net.andrewcpu.particle.world.Particle;
import net.andrewcpu.particle.world.World;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PhysicsMultiThreader {
	private World world;
	private ParticleThreader[] threads;
	private int threadPoolSize;
	private JavaShaderBridge javaShaderBridge;
	public PhysicsMultiThreader(int threadCount, World world){
		this.world = world;
		this.threads = new ParticleThreader[threadCount];
		this.threadPoolSize = this.world.getParticles().length / threadCount;
		for(int i = 0; i<threadCount; i++){
			threads[i] = new ParticleThreader(world,(i * threadPoolSize), (i + 1) * threadPoolSize);
			threads[i].start();
		}
		javaShaderBridge = JavaShaderBridge.getInstance();
		if(!javaShaderBridge.initialized){
			javaShaderBridge.initialize(world);
		}
	}

	public void dispose() {
		for(int i = 0; i<threads.length; i++){
			threads[i].stopThread();
		}
		javaShaderBridge.dispose();
	}
	private boolean query = false;

	public boolean isDoneWithPhysicsTick() {
		if(query) return false;
		for(int i = 0; i<threads.length; i++){
			if(threads[i] != null) {
				if(!threads[i].isDone()){
					return false;
				}
			}
		}
		return true;
	}

	public void updateAffections() {
		javaShaderBridge.updateAffections();
	}

	private boolean toggle = false;
	private void startPhysicsThreads() {
		toggle = !toggle;
		Particle[] particles = world.getParticles();
		query = true;
		javaShaderBridge.getDistances(Arrays.stream(particles).collect(Collectors.toList()));
		for(int i = 0; i<threads.length; i++){
			threads[i].startWork();
		}
		query = false;
	}
	public boolean doPhysicsTick() {
		if(isDoneWithPhysicsTick()){
			startPhysicsThreads();
			return true;
		}
		return false;
	}
}
