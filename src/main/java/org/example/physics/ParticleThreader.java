package org.example.physics;

import org.example.Particle;
import org.example.World;

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
						if (particles[n] == null) continue;
						particles[n].tick(world);
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
//		while(true){
//			if(!done){

//			if(shouldStop) return;
//		}

	public void startWork() {
		synchronized (lock) {
			done = false;
			lock.notify();
		}
	}

	public void stopThread() {
		shouldStop = true;
		synchronized (lock) {
			lock.notify();
		}
	}
}
