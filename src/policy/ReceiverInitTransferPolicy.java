package policy;

import jobs.Job;
import jobs.JobQueue;
import jobs.MatrixAdditionJob;
import state.State;

public class ReceiverInitTransferPolicy extends TransferPolicy{
	
	private int THRESHOLD = 2;
	private State remoteState;
	private JobQueue job_queue;
	
	public ReceiverInitTransferPolicy(JobQueue job_queue, State remoteState){
		super();
		this.job_queue = job_queue;
		this.remoteState = new State(remoteState);
	}
	
	public ReceiverInitTransferPolicy(JobQueue job_queue, State remoteState, int threshold){
		this(job_queue, remoteState);
		this.THRESHOLD = threshold;
	}
	
	public ReceiverInitTransferPolicy(JobQueue job_queue, State remoteState, int threshold, int selectionPolicy){
		this(job_queue, remoteState, threshold);
		this.THRESHOLD = threshold;
		this.selectionPolicy = selectionPolicy;
	}
	
	@Override
	public Job getJobIfTransferable() {
		if(job_queue.size() < THRESHOLD && remoteState.job_queue_length > THRESHOLD)
			return new MatrixAdditionJob(true);
		return null;
	}
}
