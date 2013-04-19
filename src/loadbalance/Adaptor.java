package loadbalance;

import java.awt.Container;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import jobs.JobQueue;
import policy.ReceiverInitTransferPolicy;
import policy.SenderInitTransferPolicy;
import policy.TransferPolicy;
import jobs.Job;
import jobs.TransferManager;
import jobs.WorkerThread;
import state.HardwareMonitor;
import state.State;
import state.StateManager;


public class Adaptor extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3881378470873496392L;
	private Container pane;
	private JLabel localLabel;
	private JLabel remoteLabel;
	private DefaultListModel localListModel;
	private DefaultListModel remoteListModel;
	
	
	State localState, remoteState;
	StateManager stateManager;
	TransferManager transferManager;
	TransferChecker transferChecker;
	HardwareMonitor hardwareMonitor;
	WorkerThread workerThread;
	
	TransferPolicy transferPolicy;
	final int THRESHOLD = 2;
	final int POLL_LIM = 1;
	
	public Adaptor(int serverPort){
		super("Load Balancer");
		workerThread = new WorkerThread();
		workerThread.start();
		stateManager = new StateManager(serverPort);
		transferManager = new TransferManager(serverPort + 1, this);
		hardwareMonitor = new HardwareMonitor();
		transferChecker = new TransferChecker();
		initGUI(serverPort);
	}
	
	private void initGUI(int serverPort){
		this.setLayout(null); 
		this.setSize(600, 400);
		pane = this.getContentPane();
		
		JLabel portLabel = new JLabel("Port No: "+serverPort);
		portLabel.setBounds(10, 0, 100, 30);
		pane.add(portLabel);
		
		localLabel = new JLabel("local node");
		localLabel.setBounds(30, 30, 100, 30);
		
		remoteLabel = new JLabel("remote node");
		remoteLabel.setBounds(330, 30, 100, 30);
		
		JList localList = new JList(localListModel = new DefaultListModel());
		localList.setBounds(30, 80, 150, 250);
		
		JList remoteList = new JList(remoteListModel = new DefaultListModel());
		remoteList.setBounds(330, 80, 150, 250);
		
		pane.add(localLabel);
		pane.add(remoteLabel);
		pane.add(remoteList);
		pane.add(localList);
		this.setVisible(true);
	}
	
	public void tryConnect(String hostname, int port){
		stateManager.tryConnect(hostname, port);
		transferManager.tryConnect(hostname, port + 1);
	}

    public JobQueue getJobQueue() {
        return workerThread.getJobQueue();
    }
    
    public synchronized void processJobRequest(){
    	if(transferPolicy == null) return;
    	
    	Job job = workerThread.getJobQueue().popIfLengthExceed(THRESHOLD, transferPolicy.selectionPolicy);
    	if(job != null)
    		transferManager.sendJob(job);
    }

    public WorkerThread getWorkerThread() {
        return workerThread;
    }
    
    public void addJob(Job job){
    	localListModel.addElement(job.toString());
    	getJobQueue().append(job);
    }

    public class TransferChecker extends Thread {
		private int SLEEP_TIME;
		
		public TransferChecker(){
			this(2000);
		}
		
		public TransferChecker(int sleep_time){
			this.SLEEP_TIME = sleep_time;
			start();
		}
		
		public synchronized void checkForAvailableTransfer(){
			localState = new state.State(workerThread.getJobQueueSize(), 0, hardwareMonitor.getCpuUtilization());
			stateManager.setState(localState);
			remoteState = stateManager.getRemoteState();
			
			// transferPolicy = (new SenderInitTransferPolicy(workerThread.getJobQueue(), remoteState));
			transferPolicy = (new ReceiverInitTransferPolicy(workerThread.getJobQueue(), remoteState));
			
			Job job = transferPolicy.getJobIfTransferable();
			if(job != null){
				transferManager.sendJob(job);
				remoteListModel.addElement(job.toString());
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
