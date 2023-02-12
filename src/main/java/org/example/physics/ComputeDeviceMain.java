package org.example.physics;

import org.jocl.*;

import static org.jocl.CL.*;

public class ComputeDeviceMain {
	public static void main(String[] args) {
		int[] numPlatformsArray = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(numPlatforms, platforms, null);

		for (int i = 0; i < numPlatforms; i++) {
			cl_platform_id platform = platforms[i];
			System.out.println("Platform " + (i + 1) + ": " + getPlatformInfoString(platform, CL_PLATFORM_NAME));

			int[] numDevicesArray = new int[1];
			clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, 0, null, numDevicesArray);
			int numDevices = numDevicesArray[0];

			cl_device_id[] devices = new cl_device_id[numDevices];
			clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, numDevices, devices, null);

			System.out.println("Number of devices: " + numDevices);
			for (int j = 0; j < numDevices; j++) {
				cl_device_id device = devices[j];
				System.out.println("Device " + (j + 1) + ": " + getDeviceInfoString(device, CL_DEVICE_NAME));
			}
		}
	}

	private static String getPlatformInfoString(cl_platform_id platform, int paramName) {
		long[] sizeArray = new long[1];
		clGetPlatformInfo(platform, paramName, 0, null, sizeArray);
		long size = sizeArray[0];

		byte[] buffer = new byte[(int)size];
		clGetPlatformInfo(platform, paramName, size, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
	}

	private static String getDeviceInfoString(cl_device_id device, int paramName) {
		long[] sizeArray = new long[1];
		clGetDeviceInfo(device, paramName, 0, null, sizeArray);
		long size = sizeArray[0];

		byte[] buffer = new byte[(int)size];
		clGetDeviceInfo(device, paramName, size, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
	}
}
