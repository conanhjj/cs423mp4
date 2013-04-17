package policy;

import jobs.Job;

public abstract class TransferPolicy {
	// Simple Selection Policy
	protected final int NEWEST = -1;
	protected final int OLDEST = -2;
	public int selectionPolicy;
	
	public TransferPolicy(){
		selectionPolicy = NEWEST;
	}
	
	public TransferPolicy(int selectionPolicy){
		this.selectionPolicy = selectionPolicy;
	}
	
	public abstract Job getJobIfTransferable();
}
