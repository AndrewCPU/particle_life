package org.example.multipliers;

public class SquareMultiplier extends ForceMultiplier{

	private double easeInOutQuart(double percent) {
		return percent < 0.5 ? 8 * percent * percent * percent * percent : 1 - Math.pow(-2 * percent + 2, 4) / 2;
	}

	@Override
	public double calculate(double min, double max, double n, double maxStrength) {
		if(n <= min){
			return minRepelForce(n, min);
		}
		if(n  >= max){
			return 0;
		}
		double percent = (n - min) / (max - min);
		if(percent <= 0.5){
			return easeInOutQuart(percent)*2;
		}
		else{
			return easeInOutQuart((1.0 - percent))*2;
		}
	}
}
