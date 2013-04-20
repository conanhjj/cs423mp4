package policy;

import jobs.Job;
import jobs.JobQueue;
import jobs.MatrixAdditionJob;
import state.State;

public class SymmetricTransferPolicy extends TransferPolicy{
	
	private int THRESHOLD = 2;
	private State remoteState;
	private JobQueue job_queue;
	
	public SymmetricTransferPolicy(JobQueue job_queue, State remoteState){
		super();
		this.job_queue = job_queue;
		this.remoteState = new State(remoteState);
	}
	
	public SymmetricTransferPolicy(JobQueue job_queue, State remoteState, int threshold){
		this(job_queue, remoteState);
		this.THRESHOLD = threshold;
	}
	
	public SymmetricTransferPolicy(JobQueue job_queue, State remoteState, int threshold, int selectionPolicy){
		this(job_queue, remoteState);
		this.THRESHOLD = threshold;
		this.selectionPolicy = selectionPolicy;
	}
	
	@Override
	public Job getJobIfTransferable() {
		if(remoteState.job_queue_length < THRESHOLD)
			return job_queue.popIfLengthExceed(THRESHOLD, NEWEST);
		else if(job_queue.size() < THRESHOLD && remoteState.job_queue_length > THRESHOLD){
				return new MatrixAdditionJob(true);
		}
		return null;
	}	
}
