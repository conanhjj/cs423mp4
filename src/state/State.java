package state;

import java.io.Serializable;

public class State implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3615574645159464691L;
	/**
	 * 
	 */
	
	public int job_queue_length;
	public int throttling;
	public double cpuUtilization;
	
	public State(int job_queue_length, int throttling, double d){
		this.job_queue_length = job_queue_length;
		this.throttling = throttling;
		this.cpuUtilization = d;
	}
	
	public State(State state){
		if(state == null)
			this.cpuUtilization = this.throttling = this.job_queue_length = -1;
		else{
			this.job_queue_length = state.job_queue_length;
			this.throttling = state.throttling;
			this.cpuUtilization = state.cpuUtilization;
		}
	}
	
	@Override
	public String toString(){
		return "state: " + job_queue_length + ' ' + throttling + ' ' + cpuUtilization;
	}
}
