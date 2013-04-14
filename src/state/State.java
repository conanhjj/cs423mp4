package state;

import java.io.Serializable;

public class State implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int job_queue_length;
	public int throttling;
	public double cpuUtilization;
	
	public State(int job_queue_length, int throttling, int cpuUtilization){
		this.job_queue_length = job_queue_length;
		this.throttling = throttling;
		this.cpuUtilization = cpuUtilization;
	}
	
	public State(State state){
		this.job_queue_length = state.job_queue_length;
		this.throttling = state.throttling;
		this.cpuUtilization = state.cpuUtilization;
	}
}
