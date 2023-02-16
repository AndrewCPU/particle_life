package net.andrewcpu.particle.world;

import net.andrewcpu.particle.constants.WorldConstant;
import net.andrewcpu.particle.physics.PhysicsMultiThreader;
import net.andrewcpu.particle.util.OpenSimplexNoise;
import net.andrewcpu.particle.enums.ParticleType;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class World {
	public Particle[] particles;
	private long tickRate = 1;
	private int size;
	private ScheduledFuture future;
	private ParticleAffection[][] affectionArray = new ParticleAffection[ParticleType.values().length][ParticleType.values().length];



	public long getTickRate() {
		return tickRate;
	}

	public int getSize() {
		return size;
	}
	private boolean disposed = false;

	public void dispose(){
		disposed = true;
		try {
			physicsMultiThreader.dispose();
			future.cancel(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isDisposed() {
		return disposed;
	}



	private void generateAffections() {
		for(ParticleType typeBase : ParticleType.values()){
			ParticleAffection[] arr = new ParticleAffection[ParticleType.values().length];
			for(ParticleType type : ParticleType.values()){
				arr[type.index] = ParticleAffection.getRandom(type);
			}
			affectionArray[typeBase.index] = arr;
		}
	}

	public ParticleAffection[][] getAffectionArray() {
		return affectionArray;
	}

	private PhysicsMultiThreader physicsMultiThreader;

	public void updateAffections() {
		physicsMultiThreader.updateAffections();
	}
	private int threads;

	public int getThreads() {
		return threads;
	}

	public World(int threads, int size) {
		this.size = size;
		this.threads = threads;
		int maxCount = size;
		int each = (int)Math.ceil((1.0 * maxCount) / ParticleType.values().length);
		particles = new Particle[each * ParticleType.values().length];
		generateAffections();
		int n = 0;
		OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise();
		double scale = Math.random() * 10000.0;
		double step = 0.5 / ParticleType.values().length;
		List<ParticleType> types = Arrays.stream(ParticleType.values()).collect(Collectors.toList());
		Collections.shuffle(types);
		_outer:
		for(double x = 0; true; x+= WorldConstant.PARTICLE_DEFAULT_SIZE+((Math.random() * 3)*WorldConstant.PARTICLE_DEFAULT_SIZE)){
			for(double y = 0; y<7500; y+=WorldConstant.PARTICLE_DEFAULT_SIZE+((Math.random() * 3)*WorldConstant.PARTICLE_DEFAULT_SIZE)){
				double value = openSimplexNoise.eval(x / scale, y / scale, scale);
				ParticleType type = ParticleType.RED;
				System.out.println(value);
				if(value >= 0){
					for(int i = 0; i<ParticleType.values().length; i++){
						if(value <= step * i){
							type = types.get((int)(Math.random() * ParticleType.values().length));
							break;
						}
					}
					particles[n] = new Particle(n, x, y,type, this);
					n++;
					if(n >= particles.length){
						break _outer;
					}
				}
			}
		}

		physicsMultiThreader = new PhysicsMultiThreader(threads,this);
		this.future = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			tick();
		}, tickRate, tickRate, TimeUnit.MILLISECONDS);
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
					times.clear();
				}
			}
			lastStart = System.currentTimeMillis();
		}
	}
}
