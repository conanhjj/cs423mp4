package loadbalance;

import jobs.Job;
import jobs.TransferManager;
import jobs.WorkerThread;
import state.HardwareMonitor;
import state.State;
import state.StateManager;


public class Adaptor {
	State state, remoteState;
	StateManager stateManager;
	TransferManager transferManager;
	HardwareMonitor hardwareMonitor;
	WorkerThread workerThread;
	final int THRESHOLD = 3;
	final int POLL_LIM = 1;
	
	public Adaptor(){
		stateManager = new StateManager();
		transferManager = new TransferManager();
		// hardwareMonitor = new HardwareManager();
		workerThread = new WorkerThread();
		workerThread.start();
	}
	
	// Sender Init
	public void checkForSenderInit(){
		int polling = 0;
		if(state.job_queue_length > THRESHOLD){
			while(polling++ < POLL_LIM){
				// Choose node randomly
				if(remoteState.job_queue_length < THRESHOLD){
					Job job = workerThread.getJobQueue().pop();
					if(job != null)
						transferManager.sendJob(job);
				}
			}
		}
	}
}
