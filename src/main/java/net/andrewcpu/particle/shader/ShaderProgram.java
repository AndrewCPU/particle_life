package net.andrewcpu.particle.shader;

import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

public abstract class ShaderProgram {
	public abstract void load() throws Exception;
	public abstract cl_mem[] getMemoryObjects();
	public abstract cl_program getProgramObject();
	public abstract String getProgram();
	public abstract cl_kernel getKernel();
	public abstract void setKernelArgs();
	public abstract void setWorkSizes();
	public abstract void execute();
	public abstract void release();
	public abstract void read(ShaderEngine engine);
}
