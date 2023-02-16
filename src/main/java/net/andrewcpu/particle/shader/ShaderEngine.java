package net.andrewcpu.particle.shader;

import org.jocl.*;

import static org.jocl.CL.*;

public class ShaderEngine {
	public final int platformIndex = 0;
	public final long deviceType = CL.CL_DEVICE_TYPE_GPU;
	public final int deviceIndex = 0;
	public int numPlatforms;
	public cl_platform_id platform;
	public cl_context_properties contextProperties;
	public cl_device_id device;
	public cl_context context;
	public cl_command_queue commandQueue;
	public void start() {
		CL.setExceptionsEnabled(true);

		int numPlatformsArray[] = new int[1];
		CL.clGetPlatformIDs(0, null, numPlatformsArray);
		numPlatforms = numPlatformsArray[0];

		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		CL.clGetPlatformIDs(platforms.length, platforms, null);
		platform = platforms[platformIndex];

		contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

		int numDevicesArray[] = new int[1];
		CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		cl_device_id devices[] = new cl_device_id[numDevices];
		CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
		device = devices[deviceIndex];

		long[] maxWorkGroupSize = new long[1];
		clGetDeviceInfo(device, CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE , Sizeof.cl_long, Pointer.to(maxWorkGroupSize), null);
		long maxWorkGroupSizeValue = maxWorkGroupSize[0];
		System.out.println("Max workgroup size " + maxWorkGroupSizeValue);


		context = CL.clCreateContext(
				contextProperties, 1, new cl_device_id[]{device},
				null, null, null);

		commandQueue =
				CL.clCreateCommandQueue(context, device, 0, null);
	}
	public void release(){
		CL.clReleaseCommandQueue(commandQueue);
		CL.clReleaseContext(context);
	}
}
