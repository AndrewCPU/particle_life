package org.example;

import org.example.physics.PhysicsMultiThreader;
import org.example.shader.HighSpeedDistanceCalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *     __local float localVectors[2 * 600];
 *     localVectors[2 * i] = vectors[2 * i];
 *     localVectors[2 * i + 1] = vectors[2 * i + 1];
 *     localVectors[2 * j] = vectors[2 * j];
 *     localVectors[2 * j + 1] = vectors[2 * j + 1];
 */
public class World {
	public Particle[] particles;
	private long tickRate = 5;
	private int size;

	public long getTickRate() {
		return tickRate;
	}

	public int getSize() {
		return size;
	}

	public void dispose(){
		future.cancel(true);
		physicsMultiThreader.dispose();
	}
	private HashMap<ParticleType, HashMap<ParticleType, ParticleAffection>> affections;
	private ScheduledFuture future;

	private ParticleAffection[][] affectionArray = new ParticleAffection[ParticleType.values().length][ParticleType.values().length];

	private void generateAffections() {
//		HashMap<ParticleType, HashMap<ParticleType, ParticleAffection>> affectionHashMap = new HashMap<>();
		for(ParticleType typeBase : ParticleType.values()){
			ParticleAffection[] arr = new ParticleAffection[ParticleType.values().length];
//			HashMap<ParticleType, ParticleAffection> affectionHashMap1 = new HashMap<>();
			for(ParticleType type : ParticleType.values()){
				arr[type.index] = ParticleAffection.getRandom(type);
//				affectionHashMap1.put(type, ParticleAffection.getRandom(type));
			}
			affectionArray[typeBase.index] = arr;
//			affectionHashMap.put(typeBase, affectionHashMap1);
		}
//		affections = affectionHashMap;
	}

	public ParticleAffection[][] getAffectionArray() {
		return affectionArray;
	}

	private PhysicsMultiThreader physicsMultiThreader;
	public World(int threads, int size) {
//		particles = new Particle[size];
		this.size = size;
		int maxCount = size;
		int each = (int)Math.ceil((1.0 * maxCount) / ParticleType.values().length);
		particles = new Particle[each * ParticleType.values().length];
		generateAffections();
		int n = 0;
		for(ParticleType type : ParticleType.values()){
			for(int i = 0; i<each; i++){
				double x = Math.random() * (2 * size);
				double y = Math.random() * size;
//				System.out.println(x + " -> " + );
				particles[n]=(new Particle(n, x, y, type, this));
				n++;
			}
//			System.out.println(n);
		}
		physicsMultiThreader = new PhysicsMultiThreader(threads,this);
		this.future = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			tick();
		}, tickRate, tickRate, TimeUnit.MILLISECONDS);
//		HighSpeedDistanceCalculation.getDistances(Arrays.stream(particles).toList());
//		HighSpeedDistanceCalculation.getDistances(Arrays.stream(particles).toList());
	}

	public Particle[] getParticles() {
		return particles;
	}

	private List<Long> times = new ArrayList<>();
	private long lastStart = -1;

	public double getAverageTime() {
		return times.stream().mapToDouble(i -> (double)i).average().getAsDouble();
	}

	public void tick() {
		if(lastStart == -1){
			lastStart = System.currentTimeMillis();
		}
		if(physicsMultiThreader.doPhysicsTick()){
			if(lastStart != -1){
				times.add(System.currentTimeMillis() - lastStart);
				if(times.size() % 50 == 0){
					System.out.println("Average physics time: " + getAverageTime() + "ms");
				}
			}
			lastStart = System.currentTimeMillis();
		}
//		times.add(System.currentTimeMillis() - start);
//		particles.forEach(particle -> particle.tick(this));
//		WorldConstant.DRAG-=0.0001;
//		WorldConstant.REPELLING_MULTIPLIER *= 0.99999999999;
	}
}
