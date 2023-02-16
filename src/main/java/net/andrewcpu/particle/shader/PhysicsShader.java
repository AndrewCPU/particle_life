package net.andrewcpu.particle.shader;

import net.andrewcpu.particle.util.ResourceLoader;
import net.andrewcpu.particle.world.World;
import net.andrewcpu.particle.world.ParticleAffection;
import net.andrewcpu.particle.enums.ParticleType;
import net.andrewcpu.particle.constants.WorldConstant;
import org.jocl.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.jocl.CL.*;

public class PhysicsShader extends ShaderProgram {
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
	private byte[] types;
	private float[] strengths;

	private cl_mem minDistanceBuffer;
	private cl_mem maxDistanceBuffer;
	private cl_mem typesBuffer;
	private cl_mem strengthsBuffer;
	public float[] resultX;
	public float[] resultY;
	public boolean hasBeenReleased = false;
	private int threadSubdivisions;

	/**
	 * Density field, buffer is kept, time decay of buffer array until 0 (to clear, but will also have cool effect)
	 * <p>
	 * densityField
	 *
	 * @param world
	 * @param shaderEngine
	 */

	public void init(World world, ShaderEngine shaderEngine) {
		this.world = world;
		this.shaderEngine = shaderEngine;
		types = new byte[world.particles.length];
		minDistances = new float[ParticleType.values().length * ParticleType.values().length];
		maxDistances = new float[ParticleType.values().length * ParticleType.values().length];
		strengths = new float[ParticleType.values().length * ParticleType.values().length];
		for (int i = 0; i < world.particles.length; i++) {
			types[world.particles[i].index] = world.particles[i].type.byteIndex;
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
		typesBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, types.length * Sizeof.cl_uchar, Pointer.to(types), null);
		threadSubdivisions = world.particles.length / 1000;
		sourceCode = getProgram();
		cl_context context = shaderEngine.context;

		program = clCreateProgramWithSource(context, 1, new String[]{sourceCode}, null, null);
		try {
			clBuildProgram(program, 0, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			release();
			System.exit(0);
		}
		kernel = clCreateKernel(program, "vec2_distance", null);
		long[] maxWorkItemSizes = new long[3];
		clGetKernelWorkGroupInfo(kernel, shaderEngine.device, CL_KERNEL_WORK_GROUP_SIZE, Sizeof.cl_long * 3, Pointer.to(maxWorkItemSizes), null);
		long maxWorkItemSizeX = maxWorkItemSizes[0];
		long maxWorkItemSizeY = maxWorkItemSizes[1];
		long maxWorkItemSizeZ = maxWorkItemSizes[2];
		System.out.println("Max work item size x " + maxWorkItemSizeX);
		System.out.println("Max work item size y " + maxWorkItemSizeY);
		System.out.println("Max work item size z " + maxWorkItemSizeZ);
	}

	public void ingestAndExecute(float[] vectors) {

		setPoints(vectors);
		setNumPoints(vectors.length / 2);


		if (hasBeenReleased) {
			throw new RuntimeException("Already released data");
		}
		initializeBuffers(vectors);
		setKernelArgs();

		setWorkSizes();
		execute();
		read(shaderEngine);

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
		return ResourceLoader.getInstance().loadTextFile("/shaders/shader.c")
				.replaceAll("\\$TYPE_AMOUNT\\$", String.valueOf(ParticleType.values().length))
				.replaceAll("\\$PARTICLE_AMOUNT_SQUARED\\$", String.valueOf(world.particles.length * world.particles.length))
				.replaceAll("\\$PARTICLE_AMOUNT\\$", String.valueOf(world.particles.length))
				.replaceAll("\\$DIVISION_AMOUNT\\$", String.valueOf(threadSubdivisions))
				.replaceAll("\\$TYPE_AMOUNT_SQ\\$", String.valueOf(ParticleType.values().length * ParticleType.values().length));
	}

	private void initializeBuffers(float[] points) {
		if (resultBufferX == null) {
			resultBufferX = CL.clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, numPoints * Sizeof.cl_float, Pointer.to(resultX), null);
			resultBufferY = CL.clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, numPoints * Sizeof.cl_float, Pointer.to(resultY), null);
		}
		if (pointsBuffer == null) {
			pointsBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, points.length * Sizeof.cl_float, Pointer.to(points), null);
		} else {
			clEnqueueWriteBuffer(shaderEngine.commandQueue, pointsBuffer, true, 0, points.length * Sizeof.cl_float, Pointer.to(points), 0, null, null);
		}
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
		clSetKernelArg(kernel, 7, Sizeof.cl_float, Pointer.to(new float[]{Double.valueOf(WorldConstant.REPELLING_MULTIPLIER).floatValue()}));
	}


	@Override
	public void setWorkSizes() {
		long[] globalWorkSize = new long[]{numPoints, threadSubdivisions};
		clEnqueueNDRangeKernel(shaderEngine.commandQueue, kernel, 2, null, globalWorkSize, null, 0, null, null);
	}

	@Override
	public void execute() {
		if (hasBeenReleased) {
			throw new RuntimeException("Data has been released.");
		}
		clEnqueueTask(shaderEngine.commandQueue, kernel, 0, null, null);
	}

	@Override
	public void release() {
		if (hasBeenReleased) {
			return;
		}
		hasBeenReleased = (true);
		clFinish(shaderEngine.commandQueue);
		if (pointsBuffer != null) {
			clReleaseMemObject(pointsBuffer);
			pointsBuffer = null;
		}
		if (resultBufferX != null) {
			clReleaseMemObject(resultBufferX);
			resultBufferX = null;
		}
		if (resultBufferY != null) {
			clReleaseMemObject(resultBufferY);
			resultBufferY = null;
		}
		if (minDistanceBuffer != null) {
			clReleaseMemObject(minDistanceBuffer);
			minDistanceBuffer = null;
		}
		if (maxDistanceBuffer != null) {
			clReleaseMemObject(maxDistanceBuffer);
			maxDistanceBuffer = null;
		}
		if (strengthsBuffer != null) {
			clReleaseMemObject(strengthsBuffer);
			strengthsBuffer = null;
		}
		if (typesBuffer != null) {
			clReleaseMemObject(typesBuffer);
			typesBuffer = null;
		}
		if (program != null) {
			clReleaseProgram(program);
			program = null;
		}
		if (kernel != null) {
			clReleaseKernel(kernel);
			kernel = null;
		}
		clFinish(shaderEngine.commandQueue);
	}

	@Override
	public void read(ShaderEngine engine) {
		if (hasBeenReleased) {
			throw new RuntimeException("Data has been released;");
		}
		clEnqueueReadBuffer(engine.commandQueue, resultBufferX, true, 0, Sizeof.cl_float * resultX.length, Pointer.to(resultX), 0, null, null);
		if (hasBeenReleased) {
			throw new RuntimeException("Data has been released;");
		}
		clEnqueueReadBuffer(engine.commandQueue, resultBufferY, true, 0, Sizeof.cl_float * resultY.length, Pointer.to(resultY), 0, null, null);
//		clEnqueueReadBuffer(engine.commandQueue, internalClockBuffer, true, 0, Sizeof.cl_float * resultY.length, Pointer.to(internalClock), 0, null, null);
		if (hasBeenReleased) {
			throw new RuntimeException("Data has been released;");
		}
		clFinish(engine.commandQueue);
	}


	public void setPoints(float[] points) {
		if (pointsBuffer == null || numPoints != points.length / 2) {
			this.points = new float[points.length];
			System.arraycopy(points, 0, this.points, 0, points.length);
		} else {
			System.arraycopy(points, 0, this.points, 0, points.length);
		}
	}

	public void setNumPoints(int numPoints) {
		if (this.resultBufferX == null || numPoints != this.resultX.length) {
			this.numPoints = numPoints;
			this.resultX = new float[numPoints];
			this.resultY = new float[numPoints];
		}
	}

	public void updateAffections() {
		if (strengthsBuffer != null) {
			clReleaseMemObject(strengthsBuffer);
		}
		int typeAmount = ParticleType.values().length;
		for (ParticleType type : ParticleType.values()) {
			ParticleAffection[] affections = world.getAffectionArray()[type.index];
			for (int i = 0; i < affections.length; i++) {
				strengths[type.index * typeAmount + i] = (float) affections[i].strength;
			}
		}
		strengthsBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, strengths.length * Sizeof.cl_float, Pointer.to(strengths), null);
	}
}
