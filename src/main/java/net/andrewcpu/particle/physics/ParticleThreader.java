package net.andrewcpu.particle.physics;

import net.andrewcpu.particle.world.Particle;
import net.andrewcpu.particle.world.World;

public class ParticleThreader extends Thread {
	private World world;
	private int min;
	private int max;
	private boolean done = true;
	private Particle[] particles;

	private Object lock = new Object();
	private volatile boolean shouldStop = false;


	public ParticleThreader(World world, int min, int max) {
		this.world = world;
		this.min = min;
		this.max = max;
		this.particles = world.getParticles();
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isDone() {
		return done;
	}


	@Override
	public void run() {

		while (!shouldStop) {
			synchronized (lock) {
				if(!done){
					for (int n = min; n <= max; n++) {
						if(n >= particles.length) break;
						if (particles[n] == null) continue;
						particles[n].tick();
					}
				}
				done = true;
				try {
					lock.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	public void startWork() {
		synchronized (lock) {
			done = false;
			lock.notify();
		}
	}

	public void stopThread() {
		shouldStop = true;
		done = true;
		synchronized (lock) {
			lock.notify();
		}
	}
}
