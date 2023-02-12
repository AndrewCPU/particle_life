package org.example.shader;

import org.jocl.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_HOST_PTR;

public class MainProgram extends ShaderProgram{
	public String code;
	public cl_program program;
	public cl_mem[] memObjects;
	public cl_context context;
	public cl_kernel kernel;
	public ShaderEngine engine;
	public float[] vectors;
	public float[] distances;
	public Pointer vectorPointer;
	public Pointer distancePointer;
	public Pointer numVectorsPointer;
//	private cl_program program;

	public void setup(ShaderEngine engine) throws Exception {
		this.context = engine.context;
		this.engine = engine;
		load();
	}

	public float[] ingestAndExecute(float[] vectors) {
		distances(vectors);

		getMemoryObjects();
		getProgramObject();
		getKernel();
		setKernelArgs();
		setWorkSizes();
		execute();
		read(engine);
		return this.distances;
	}


	public float[] distances(float[] vectors) {
		this.vectors = vectors;
		this.distances = new float[(vectors.length / 2) * (vectors.length / 2)];
		this.vectorPointer = Pointer.to(this.vectors);
		this.distancePointer = Pointer.to(this.distances);
		this.numVectorsPointer = Pointer.to(new int[]{this.vectors.length/2});
//		return
		return distances;
	}

	@Override
	public void load() throws Exception {
		this.code = new String(Files.readAllBytes(new File("shaders/main.shad").toPath()));
		program = CL.clCreateProgramWithSource(context, 1, new String[]{code}, null, null);
	}

	@Override
	public cl_mem[] getMemoryObjects() {
		memObjects = new cl_mem[3];
		memObjects[0] = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_float * vectors.length, vectorPointer, null);
		memObjects[1] = CL.clCreateBuffer(context, CL.CL_MEM_WRITE_ONLY,
				Sizeof.cl_float * distances.length, null, null);
		memObjects[2] = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY,
				Sizeof.cl_int, null, null);

		CL.clEnqueueWriteBuffer(engine.commandQueue, memObjects[0], CL.CL_TRUE, 0,
				vectors.length * Sizeof.cl_float, vectorPointer, 0, null, null);
		CL.clEnqueueWriteBuffer(engine.commandQueue, memObjects[2], CL.CL_TRUE, 0,
				Sizeof.cl_int, numVectorsPointer, 0, null, null);

		return memObjects;
	}


	@Override
	public cl_program getProgramObject() {
		program = CL.clCreateProgramWithSource(context,
				1, new String[]{ code }, null, null);
		CL.clBuildProgram(program, 0, null, null, null, null);
		return program;
	}

	@Override
	public String getProgram() {
		return code;
	}

	@Override
	public cl_kernel getKernel() {
		kernel = CL.clCreateKernel(program, "vec2_distance", null);
		return kernel;
	}

	@Override
	public void setKernelArgs() {
		CL.clSetKernelArg(kernel, 0,
				Sizeof.cl_mem, Pointer.to(memObjects[0]));
		CL.clSetKernelArg(kernel, 1,
				Sizeof.cl_mem, Pointer.to(memObjects[1]));
		CL.clSetKernelArg(kernel, 2,
				Sizeof.cl_int, Pointer.to(memObjects[2]));
	}

	@Override
	public void setWorkSizes() {

	}

	@Override
	public void execute() {
		CL.clEnqueueNDRangeKernel(engine.commandQueue, kernel, 2, null,
				new long[]{vectors.length / 2, vectors.length / 2}, null, 0, null, null);
	}

	@Override
	public void release() {
		CL.clReleaseMemObject(memObjects[0]);
		CL.clReleaseMemObject(memObjects[1]);
		CL.clReleaseMemObject(memObjects[2]);
		CL.clReleaseKernel(kernel);
		CL.clReleaseProgram(program);
	}

	@Override
	public void read(ShaderEngine engine) {
		CL.clFinish(engine.commandQueue);
		CL.clEnqueueReadBuffer(engine.commandQueue, memObjects[1], CL.CL_TRUE, 0,
				distances.length * Sizeof.cl_float, distancePointer, 0, null, null);
	}
}
