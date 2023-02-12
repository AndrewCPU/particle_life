package org.example.physics;

import org.example.Particle;
import org.example.World;
import org.example.shader.HighSpeedDistanceCalculation;
import org.example.shader.ShaderEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PhysicsMultiThreader {
	private World world;
	private ParticleThreader[] threads;
	private int threadPoolSize;
	private HighSpeedDistanceCalculation highSpeedDistanceCalculation;
	public PhysicsMultiThreader(int threadCount, World world){
		this.world = world;
		this.threads = new ParticleThreader[threadCount];
		this.threadPoolSize = this.world.getParticles().length / threadCount;
		for(int i = 0; i<threadCount; i++){
			threads[i] = new ParticleThreader(world,(i * threadPoolSize), (i + 1) * threadPoolSize);
			threads[i].start();
		}
		highSpeedDistanceCalculation = HighSpeedDistanceCalculation.getInstance();
		if(!highSpeedDistanceCalculation.initialized){
			highSpeedDistanceCalculation.initialize(world);
		}
	}

	public void dispose() {
		for(int i = 0; i<threads.length; i++){
			threads[i].stopThread();
		}
		highSpeedDistanceCalculation.dispose();
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

	private boolean toggle = false;
	private void startPhysicsThreads() {
		toggle = !toggle;
		Particle[] particles = world.getParticles();
		query = true;
		long start = System.currentTimeMillis();
		highSpeedDistanceCalculation.getDistances(Arrays.stream(particles).collect(Collectors.toList()));
//		System.out.println((System.currentTimeMillis() - start) + "ms ");
		for(int i = 0; i<particles.length; i++){
			if(particles[i] == null) continue;
			particles[i].tick(world);
		}

//		for(int i = 0; i<threads.length; i++){
//			threads[i].startWork();
//		}
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
