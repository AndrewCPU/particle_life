package org.example.shader;

import org.example.ParticleAffection;
import org.example.ParticleType;
import org.example.World;
import org.jocl.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import static org.jocl.CL.*;

public class PhysicsShaderv2 extends ShaderProgram {
	private cl_mem pointsBuffer;
	private cl_mem resultBufferX;
	private cl_mem resultBufferY;
	private cl_program program;
	private cl_kernel kernel;
	private int numPoints;
	private float[] points;
	//	private float[] result;
	private ShaderEngine shaderEngine;
	private String sourceCode;
	private World world;
	private float[] minDistances;
	private float[] maxDistances;
	private int[] types;
	private float[] strengths;

	private cl_mem minDistanceBuffer;
	private cl_mem maxDistanceBuffer;
	private cl_mem typesBuffer;
	private cl_mem strengthsBuffer;
	public float[] resultX;
	public float[] resultY;

	public void init(World world, ShaderEngine shaderEngine) {
		this.world = world;
		this.shaderEngine = shaderEngine;
		types = new int[world.particles.length];
		minDistances = new float[ParticleType.values().length * ParticleType.values().length];
		maxDistances = new float[ParticleType.values().length * ParticleType.values().length];
		strengths = new float[ParticleType.values().length * ParticleType.values().length];

		for (int i = 0; i < world.particles.length; i++) {
			types[world.particles[i].index] = world.particles[i].type.index;
		}

		int typeAmount = ParticleType.values().length;
		for (ParticleType type : ParticleType.values()) {
			ParticleAffection[] affections = world.getAffectionArray()[type.index];
			for (int i = 0; i < affections.length; i++) {
				minDistances[type.index * typeAmount + i] = (float) affections[i].minimumDistance;
				maxDistances[type.index * typeAmount + i] = (float) affections[i].maximumDistance;
				strengths[type.index * typeAmount + i] = (float) affections[i].strength;
			}
		}

		minDistanceBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, minDistances.length * Sizeof.cl_float, Pointer.to(minDistances), null);
		maxDistanceBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, maxDistances.length * Sizeof.cl_float, Pointer.to(maxDistances), null);
		strengthsBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, strengths.length * Sizeof.cl_float, Pointer.to(strengths), null);
		typesBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, types.length * Sizeof.cl_int, Pointer.to(types), null);

		sourceCode = getProgram();
		cl_context context = shaderEngine.context;

		program = clCreateProgramWithSource(context, 1, new String[]{sourceCode}, null, null);
		clBuildProgram(program, 0, null, null, null, null);
		kernel = clCreateKernel(program, "vec2_distance", null);
	}

	public void ingestAndExecute(float[] vectors) {
		int oldLen = this.points == null ? -1 : this.points.length;
		setPoints(vectors);
		setNumPoints(vectors.length / 2);
		initializeBuffers(vectors);
		setKernelArgs();

		setWorkSizes();
		execute();
		read(shaderEngine);

//		float[] results = new float[numPoints * numPoints*2];
//		javaCalculation(vectors, minDistances,maxDistances, strengths,types, results,numPoints,ParticleType.values().length);
//		result = results;
		return;
	}

	@Override
	public void load() throws Exception {

	}

	@Override
	public cl_mem[] getMemoryObjects() {
		return new cl_mem[]{pointsBuffer, minDistanceBuffer, maxDistanceBuffer, strengthsBuffer, typesBuffer, resultBufferX, resultBufferY};
	}

	@Override
	public cl_program getProgramObject() {
		return program;
	}

	@Override
	public String getProgram() {
		try {
			return new String(Files.readAllBytes(new File("shaders/shader2.shad").toPath()))
					.replaceAll("\\$TYPE_AMOUNT_SQUARED\\$",String.valueOf(ParticleType.values().length * ParticleType.values().length))
					.replaceAll("\\$PARTICLE_AMOUNT_SQUARED\\$", String.valueOf(world.particles.length * world.particles.length))
					.replaceAll("\\$PARTICLE_AMOUNT\\$", String.valueOf(world.particles.length));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void initializeBuffers(float[] points) {
		if (resultBufferX == null) {
			resultBufferX = CL.clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, numPoints * Sizeof.cl_float, Pointer.to(resultX), null);
			resultBufferY = CL.clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, numPoints * Sizeof.cl_float, Pointer.to(resultY), null);
		}
//		Arrays.fill(resultX,0);
//		Arrays.fill(resultY,0);
//		int npsq = numPoints * numPoints;
//		for(int i = 0; i<npsq; i++){
//			resultX[i] = 0;
//			resultY[i] = 0;
//		}

//		clEnqueueFillBuffer(shaderEngine.commandQueue, resultBuffer, Pointer.to(new float[]{0}), Sizeof.cl_float, 0, result.length, 0, null, null);
//		clFinish(shaderEngine.commandQueue);
		long start = System.currentTimeMillis();
		if(pointsBuffer != null){
			clReleaseMemObject(pointsBuffer);
		}
		pointsBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, points.length * Sizeof.cl_float, Pointer.to(points), null);
//		System.out.println("LL -> " + (System.currentTimeMillis() - start) + "ms");
//		if (pointsBuffer == null) {
//		}
//		else{
//			clEnqueueFillBuffer(shaderEngine.commandQueue, pointsBuffer, Pointer.to(points),Sizeof.cl_float, 0, points.length * Sizeof.cl_float,0, null, null);
//			clFinish(shaderEngine.commandQueue);
//		}
//		clFinish(shaderEngine.commandQueue);
//		clEnqueueWriteBuffer(shaderEngine.commandQueue, pointsBuffer, CL_TRUE, 0, points.length * Sizeof.cl_float,Pointer.to(points), 0, null, null);
//		clFinish(shaderEngine.commandQueue);
	}

	@Override
	public cl_kernel getKernel() {
		return kernel;
	}

	@Override
	public void setKernelArgs() {
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(pointsBuffer));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(minDistanceBuffer));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(maxDistanceBuffer));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(strengthsBuffer));
		clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(typesBuffer));
		clSetKernelArg(kernel, 5, Sizeof.cl_mem, Pointer.to(resultBufferX));
		clSetKernelArg(kernel, 6, Sizeof.cl_mem, Pointer.to(resultBufferY));
		clSetKernelArg(kernel, 7, Sizeof.cl_int, Pointer.to(new int[]{numPoints}));
		clSetKernelArg(kernel, 8, Sizeof.cl_int, Pointer.to(new int[]{ParticleType.values().length}));
	}


	@Override
	public void setWorkSizes() {
		long[] globalWorkSize = new long[]{numPoints};
		clEnqueueNDRangeKernel(shaderEngine.commandQueue, kernel, 1, null, globalWorkSize, null, 0, null, null);
		clFinish(shaderEngine.commandQueue);

	}

	@Override
	public void execute() {
		clEnqueueTask(shaderEngine.commandQueue, kernel, 0, null, null);
		clFinish(shaderEngine.commandQueue);
	}

	@Override
	public void release() {
		clReleaseMemObject(pointsBuffer);
		clReleaseMemObject(resultBufferX);
		clReleaseMemObject(resultBufferY);
		clReleaseMemObject(minDistanceBuffer);
		clReleaseMemObject(maxDistanceBuffer);
		clReleaseMemObject(strengthsBuffer);
		clReleaseMemObject(typesBuffer);
		clReleaseProgram(program);
		clReleaseKernel(kernel);
	}

	@Override
	public void read(ShaderEngine engine) {
//		ByteBuffer arrayPointer = clEnqueueMapBuffer(engine.commandQueue, resultBufferX, CL_TRUE, CL_MAP_READ, 0, Sizeof.cl_float * resultX.length, 0, null, null, null);
//		ByteBuffer arrayPointer2 = clEnqueueMapBuffer(engine.commandQueue, resultBufferY, CL_TRUE, CL_MAP_READ, 0, Sizeof.cl_float * resultY.length, 0, null, null, null);
//		clEnqueueUnmapMemObject(engine.commandQueue, resultBufferX, arrayPointer, 0, null, null);
//		clEnqueueUnmapMemObject(engine.commandQueue, resultBufferY, arrayPointer2, 0, null, null);
//		clFinish(engine.commandQueue);
		clEnqueueReadBuffer(engine.commandQueue, resultBufferX, true, 0, Sizeof.cl_float * resultX.length, Pointer.to(resultX), 0, null, null);
		clEnqueueReadBuffer(engine.commandQueue, resultBufferY, true, 0, Sizeof.cl_float * resultY.length, Pointer.to(resultY), 0, null, null);
		clFinish(engine.commandQueue);
	}

//	private Pointer hostPointer;

	public void setPoints(float[] points) {
		if (pointsBuffer == null || numPoints != points.length / 2) {
			this.points = new float[points.length];
			System.arraycopy(points, 0, this.points, 0, points.length);
//			this.points = points;
		} else {
			this.points = new float[points.length];
			System.arraycopy(points, 0, this.points, 0, points.length);
		}
	}

	public void setNumPoints(int numPoints) {
		if (this.resultBufferX == null || numPoints != this.resultX.length) {
			this.numPoints = numPoints;
			this.resultX = new float[numPoints];
			this.resultY = new float[numPoints];
//			resultBuffer = clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE, Sizeof.cl_float * result.length, null, null);

		}
	}
}
