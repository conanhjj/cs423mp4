package jobs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Transfer Manager: The Transfer Manager is responsible of performing a load transfer 
 * upon request of the adaptor. It must move jobs from the Job Queue and send them to remote node. 
 * It must also receive any jobs sent by the remote node and place them in the local Job Queue. 
 * You can use any protocol that you choose (e.g. TCP, UDP, HTTP, etc.)
 */

public class TransferManager {
	public Socket socket;
	public String hostname;
	public Listener listener;
	public int port;
	
	public TransferManager(){
		hostname = "10.10.10.1";
		port = 4567;

		try {    
			socket = new Socket(hostname, port);
			listener = new Listener(this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void sendJob(Job job){
		// remove from job queue
		try 
        {
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(job);                
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
	}
	
	public void receiveJob(Job job){
		// put into job queue
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
			while(true)	{
				// read message from socket;
				try {
					ObjectInputStream objectInput = new ObjectInputStream(transferManager.socket.getInputStream());
	                try {
	                    Job job = (Job) objectInput.readObject();
	                    transferManager.receiveJob(job);
	                } catch (ClassNotFoundException e) {
	                    e.printStackTrace();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
		}
	}
}