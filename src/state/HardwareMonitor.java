package state;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.management.OperatingSystemMXBean;


public class HardwareMonitor {
	/*
	 * ref: http://stackoverflow.com/questions/5907519/measure-cpu-usage-of-the-jvm-java-code
	 */
	public static double getCpuUtilzation(){
		
		OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	    int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
	    long prevUpTime = runtimeMXBean.getUptime();
	    long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
	    double cpuUsage;
	    
	    // Throtling ???
	    try 
	    {
	        Thread.sleep(500);
	    } 
	    catch (Exception ignored) { }

	    operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	    long upTime = runtimeMXBean.getUptime();
	    long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
	    long elapsedCpu = processCpuTime - prevProcessCpuTime;
	    long elapsedTime = upTime - prevUpTime;

	    cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
	    return cpuUsage;
	}
	
	public static void main(String [] args) {
		System.out.println(getCpuUtilzation());
	}
}
