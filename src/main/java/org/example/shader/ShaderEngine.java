package org.example.shader;

import org.jocl.*;

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
//		final int platformIndex = 0;
//		final long deviceType = CL.CL_DEVICE_TYPE_ALL;
//		final int deviceIndex = 0;
		CL.setExceptionsEnabled(true);

		int numPlatformsArray[] = new int[1];
		CL.clGetPlatformIDs(0, null, numPlatformsArray);
		numPlatforms = numPlatformsArray[0];

		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		CL.clGetPlatformIDs(platforms.length, platforms, null);
		platform = platforms[platformIndex];

		// Initialize the context properties
		contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		// Obtain a device ID
		cl_device_id devices[] = new cl_device_id[numDevices];
		CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
		device = devices[deviceIndex];

		// Create a context for the selected device
		context = CL.clCreateContext(
				contextProperties, 1, new cl_device_id[]{device},
				null, null, null);

		// Create a command-queue for the selected device
		commandQueue =
				CL.clCreateCommandQueue(context, device, 0, null);

		// Allocate the memory objects for the input and output data


		// Create the program from the source code


		// Build the program

		// Create the kernel

		// Set the arguments for the kernel


		// Set the work-item dimensions


		// Execute the kernel


		// Read the output data

		// Release kernel, program, and memory objects



	}
	public void release(){
		CL.clReleaseCommandQueue(commandQueue);
		CL.clReleaseContext(context);
	}
}
