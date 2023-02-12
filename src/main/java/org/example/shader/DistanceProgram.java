package org.example.shader;

import org.jocl.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.jocl.CL.*;

public class DistanceProgram extends ShaderProgram {
	private cl_mem pointsBuffer;
	private cl_mem resultBuffer;
	private cl_program program;
	private cl_kernel kernel;
	private int numPoints;
	private float[] points;
	private float[] result;
	private ShaderEngine shaderEngine;
	private String sourceCode;

	public void init(ShaderEngine shaderEngine) {
		this.shaderEngine = shaderEngine;
		sourceCode = getProgram();
		cl_context context = shaderEngine.context;

		program = clCreateProgramWithSource(context, 1, new String[]{sourceCode}, null, null);
		clBuildProgram(program, 0, null, null, null, null);
		kernel = clCreateKernel(program, "vec2_distance", null);
	}

	public float[] ingestAndExecute(float[] vectors) {
		setPoints(vectors);
		setNumPoints(vectors.length / 2);
		initializeBuffers();
		setKernelArgs();
		setWorkSizes();
		execute();
		read(shaderEngine);
		return this.result;
	}

	@Override
	public void load() throws Exception {

	}

	@Override
	public cl_mem[] getMemoryObjects() {
		return new cl_mem[]{pointsBuffer, resultBuffer};
	}

	@Override
	public cl_program getProgramObject() {
		return program;
	}

	@Override
	public String getProgram() {
		try {
			return new String(Files.readAllBytes(new File("shaders/main.shad").toPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void initializeBuffers() {
		if(resultBuffer != null){
			clReleaseMemObject(resultBuffer);
		}
		resultBuffer = CL.clCreateBuffer(shaderEngine.context, CL_MEM_WRITE_ONLY, numPoints * numPoints * Sizeof.cl_float,null,null);
//		clEnqueueFillBuffer(shaderEngine.commandQueue, resultBuffer, Pointer.to(new float[]{0}), Sizeof.cl_float, 0, result.length, 0, null, null);
//		clFinish(shaderEngine.commandQueue);
		if(pointsBuffer != null){
			clReleaseMemObject(pointsBuffer);
		}
		pointsBuffer = CL.clCreateBuffer(shaderEngine.context, CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, points.length * Sizeof.cl_float, Pointer.to(points), null);
//		clEnqueueFillBuffer(shaderEngine.commandQueue, pointsBuffer, Pointer.to(new float[]{0}),Sizeof.cl_float, 0, points.length,0, null, null);
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
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(resultBuffer));
		clSetKernelArg(kernel, 2, Sizeof.cl_int, Pointer.to(new int[]{numPoints}));
	}

	@Override
	public void setWorkSizes() {
		long[] globalWorkSize = new long[]{numPoints, numPoints};
		clEnqueueNDRangeKernel(shaderEngine.commandQueue, kernel, 2, null, globalWorkSize, null, 0, null, null);
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
		clReleaseMemObject(resultBuffer);
		clReleaseProgram(program);
		clReleaseKernel(kernel);
	}

	@Override
	public void read(ShaderEngine engine) {
		clEnqueueReadBuffer(engine.commandQueue, resultBuffer, true, 0, Sizeof.cl_float * result.length, Pointer.to(result), 0, null, null);
		clFinish(engine.commandQueue);
	}
	private Pointer hostPointer;

	public void setPoints(float[] points) {
		if(pointsBuffer == null || numPoints != points.length / 2){
			if(pointsBuffer != null){
				clReleaseMemObject(pointsBuffer);
			}
			this.points = new float[points.length];
			System.arraycopy(points, 0, this.points, 0, points.length);
//			this.points = points;
		}
		else{
			this.points = new float[points.length];
			System.arraycopy(points, 0, this.points, 0, points.length);
		}
	}
	public
	float[] getResult() {
		return result;
	}

	public void setNumPoints(int numPoints) {
		if(result == null || numPoints * numPoints != this.result.length){
			this.numPoints = numPoints;
			if(resultBuffer != null){
				clReleaseMemObject(resultBuffer);
			}
			this.result = new float[numPoints*numPoints];
			resultBuffer = clCreateBuffer(shaderEngine.context, CL_MEM_READ_WRITE, Sizeof.cl_float * result.length, null, null);

		}
	}
}
