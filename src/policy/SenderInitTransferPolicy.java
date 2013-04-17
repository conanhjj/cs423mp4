package policy;

import state.State;
import jobs.Job;
import jobs.JobQueue;

public class SenderInitTransferPolicy extends TransferPolicy {
	
	private int THRESHOLD = 2;
	private State remoteState;
	private JobQueue job_queue;
	
	public SenderInitTransferPolicy(JobQueue job_queue, State remoteState){
		super();
		this.job_queue = job_queue;
		this.remoteState = new State(remoteState);
	}
	
	public SenderInitTransferPolicy(JobQueue job_queue, State remoteState, int threshold){
		this(job_queue, remoteState);
		this.THRESHOLD = threshold;
	}
	
	public SenderInitTransferPolicy(JobQueue job_queue, State remoteState, int threshold, int selectionPolicy){
		this(job_queue, remoteState);
		this.THRESHOLD = threshold;
		this.selectionPolicy = selectionPolicy;
	}
	
	@Override
	public Job getJobIfTransferable() {
		if(remoteState.job_queue_length < THRESHOLD)
			return job_queue.popIfLengthExceed(THRESHOLD, NEWEST);
		return null;
	}
}
