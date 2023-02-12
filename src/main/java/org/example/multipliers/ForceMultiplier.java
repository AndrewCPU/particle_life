package org.example.multipliers;

import org.example.WorldConstant;

public abstract class ForceMultiplier {
	public abstract double calculate(double min, double max, double n, double maxStrength);
	double minRepelForce(double n, double min){
		double y = (1.0 / n) - 1.0;
		return -(y* WorldConstant.REPELLING_MULTIPLIER);
	}

}
