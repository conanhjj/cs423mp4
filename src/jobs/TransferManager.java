package jobs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import loadbalance.Adaptor;

/*
 * Transfer Manager: The Transfer Manager is responsible of performing a load transfer 
 * upon request of the adaptor. It must move jobs from the Job Queue and send them to remote node. 
 * It must also receive any jobs sent by the remote node and place them in the local Job Queue. 
 * You can use any protocol that you choose (e.g. TCP, UDP, HTTP, etc.)
 */

public class TransferManager {
	public Socket socket;
	private Listener listener;
	private Adaptor adaptor;
	private int PORT_NO = 20001;
	public boolean isRunning;
	
	public TransferManager(int serverPort, Adaptor adaptor){
		PORT_NO = serverPort;
		this.adaptor = adaptor;
		new ServerListener(this);
	}
	
	public void init(){
		isRunning = true;
		listener = new Listener(this);
	}
	
	public void tryConnect(String hostname, int port){
		try {
			socket = new Socket(hostname, port);
			init();
			System.out.println("init transferManager in tryConnect");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sendJob(Job job){
		if(job.isRequest)
			System.out.println("Send job Request");
		try 
        {
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(job);
            objectOutput.flush();
        } 
        catch (IOException e) 
        {
            //e.printStackTrace();
        } 
	}
	
	/*
	public void requestJob(){
		System.out.println("Send request.");
		sendJob(new Job(true));
	}
	*/
	
	public void receiveRequest(){
		adaptor.processJobRequest();
	}
	
	public void receiveJob(Job job){
		// put into job queue via Adaptor
		System.out.println("Received Job");
		adaptor.n_job_transfer++;
		if(job.isFinished())
			adaptor.jobFinished(job);
		else
			adaptor.addJob(job);
	}
	
	/*
	 * ref: http://stackoverflow.com/questions/959785/java-sockets-asynchronous-wait-synchronous-read
	 */
	public class Listener extends Thread {
		TransferManager transferManager;
		public Listener(TransferManager tm) {

		    // initialize thread resources (sockets, database connections, etc)
			this.transferManager = tm;
		    start();
		    Thread.yield();
		}

		public void run() {
			while(isRunning)	{
				// read message from socket;
				try {
					ObjectInputStream objectInput = new ObjectInputStream(transferManager.socket.getInputStream());
	                try {
	                    Job job = (Job) objectInput.readObject();
	                    if(job.isRequest)
	                    	transferManager.receiveRequest();
	                    else
	                    	transferManager.receiveJob(job);
	                } catch (ClassNotFoundException e) {
	                    e.printStackTrace();
	                }
				} catch (IOException e) {
					System.out.println("Transfer Manager's socket: Disconnected.");
					break;
				}  
			}
		}
	}
	
	public class ServerListener extends Thread {
		TransferManager transferManager;
		public ServerListener(TransferManager tm){
			this.transferManager = tm;
			start();
			Thread.yield();
		}
		public void run() {
			try {    
				ServerSocket serverSocket = new ServerSocket(PORT_NO);
				socket = serverSocket.accept();
				init();
				System.out.println("init transferManager in ServerListener");
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}


}
