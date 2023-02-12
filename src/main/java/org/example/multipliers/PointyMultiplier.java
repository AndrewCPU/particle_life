package org.example.multipliers;

import org.example.WorldConstant;

public class PointyMultiplier extends ForceMultiplier{


	@Override
	public double calculate(double min, double max, double n, double maxStrength) {
//		double n = Math.sqrt(n_sq);
		if(n < min){
			return minRepelForce(n, min);
		}
		if(n > max){
			return 0;
		}
//		double peakPower = (max + min) / 2.0; //THE MIDPOINT. THIS CAN BE CHANGED. (NOT A PERCENT. ACTUAL DISTANCE).
		// 0 is minpower
		double fm = (n - min) / (max - min);
		double value = Math.sin(fm * Math.PI);
		return maxStrength * value;
		// 1 max distance
	}
}
