package loadbalance;

import jobs.JobQueue;
import policy.SenderInitTransferPolicy;
import policy.TransferPolicy;
import jobs.Job;
import jobs.TransferManager;
import jobs.WorkerThread;
import state.HardwareMonitor;
import state.State;
import state.StateManager;


public class Adaptor {
	State localState, remoteState;
	StateManager stateManager;
	TransferManager transferManager;
	TransferChecker transferChecker;
	HardwareMonitor hardwareMonitor;
	WorkerThread workerThread;
	final int THRESHOLD = 3;
	final int POLL_LIM = 1;
	
	public Adaptor(int serverPort){
		workerThread = new WorkerThread();
		workerThread.start();
		stateManager = new StateManager(serverPort);
		transferManager = new TransferManager(serverPort + 1, this);
		transferChecker = new TransferChecker();
		hardwareMonitor = new HardwareMonitor();
	}
	
	public void tryConnect(String hostname, int port){
		stateManager.tryConnect(hostname, port);
		transferManager.tryConnect(hostname, port + 1);
	}

    public JobQueue getJobQueue() {
        return workerThread.getJobQueue();
    }

    public WorkerThread getWorkerThread() {
        return workerThread;
    }
    
    public void addJob(Job job){
    	getJobQueue().append(job);
    }

    public class TransferChecker extends Thread {
		private int SLEEP_TIME;
		
		public TransferChecker(){
			this(1000);
		}
		
		public TransferChecker(int sleep_time){
			this.SLEEP_TIME = sleep_time;
			start();
		}
		
		public void checkForAvailableTransfer(){
			localState = new state.State(workerThread.getJobQueueSize(), 0, 20);
			stateManager.setState(localState);
			remoteState = stateManager.getRemoteState();
			
			TransferPolicy transferPolicy = (new SenderInitTransferPolicy(localState.job_queue_length));
			if(transferPolicy.isTransferable()){
				
				if(remoteState.job_queue_length < THRESHOLD){
					Job job = workerThread.getJobQueue().pop();
					if(job != null)
						transferManager.sendJob(job);
				}
				//if(node != null)
				//transferManager.sendJob(job);
			}
		}
		
		@Override
		public void run() {
			while(true){
				this.checkForAvailableTransfer();
				try {
					sleep(this.SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
