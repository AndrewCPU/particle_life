package org.example.shader;

import java.text.DecimalFormat;

public class ShaderTest {
	public static void main(String[] args) {
		ShaderEngine engine = new ShaderEngine();
		engine.start();
		DistanceProgram mainProgram = new DistanceProgram();
		try {
			mainProgram.init(engine);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int amountOfVectors = 16;
		for(int nn = 0; nn<2; nn++){
			float[] flt = new float[2 * amountOfVectors];

			for(int i = 0; i<flt.length; i+=2){
				flt[i] = (float)Math.random() * 50.0f;
				flt[i+1] = (float)Math.random() * 50.0f;
			}
			for(float f : flt) System.out.println(f);
			float[] output = mainProgram.ingestAndExecute(flt);
			float[] result = mainProgram.getResult();
//		float[] distances = mainProgram.ingestAndExecute(flt);
			for(float f : result) System.out.println(f);
			for(float f : output) System.out.println(f);


			for(int i = 0; i<amountOfVectors; i+=2){
				for(int j = 1; j<amountOfVectors; j+=2){
					int first = i / 2;
					int second = j / 2;
					double dx = flt[first*2] - flt[second*2];
					double dy = flt[first*2+1] - flt[second*2+1];
					double testValue = Math.sqrt(dx * dx + dy * dy);

					double outputValue = result[first * (amountOfVectors) + second];
					System.out.println(new DecimalFormat("00.000000").format(testValue - outputValue));
					System.out.println("                   " + testValue + " == ? " + outputValue);
				}
			}
		}

		mainProgram.release();
		engine.release();



	}
}
