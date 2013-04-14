package state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * State Manager: The State Manager is responsible of transferring system state to the remote node, 
 * including, the number of jobs pending in the queue, current local throttling values, 
 * and all the information collected by the Hardware Monitor. 
 * For this component, you will need to choose an Information Policy. 
 * This policy will dictate how often the collected information is exchanged including time intervals or events. 
 * Careful design of this policy is important due to the performance, stability and overhead tradeoffs.
 */

public class StateManager {
	int interval;
	public Socket socket;
	private State state;
	private State remoteState;
	private LoopSender loopSender;
	private Listener listener;
	final private int PORT_NO = 4567;
	
	public StateManager(int interval) {
		this.interval = interval;
		new ServerListener(this);
	}
	
	public StateManager(){
		this(10000);
	}
	
	public synchronized void setState(State state){
		state = new State(state);
	}
	
	public synchronized void setRemoteState(State state){
		this.remoteState = new State(state);
	}
	
	private synchronized void sendState(){
		try 
        {
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(state);                
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
	}
	
	public State getRemoteState(){
		return this.remoteState;
	}
	
	public class LoopSender extends Thread {
		StateManager stateManager;
		public LoopSender(StateManager sm) {
			this.stateManager = sm;
			
		    start();
		    Thread.yield();
		}

		public void run() {
			while(true)	{
				
				stateManager.sendState();
				
				try {
					sleep(stateManager.interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public class Listener extends Thread {
		StateManager stateManager;
		public Listener(StateManager sm) {
			this.stateManager = sm;
		    start();
		    Thread.yield();
		}

		public void run() {
			while(true)	{
				// read message from socket;
				try {
					ObjectInputStream objectInput = new ObjectInputStream(stateManager.socket.getInputStream());
	                try {
	                    state.State state = (state.State) objectInput.readObject();
	                    stateManager.setRemoteState(state);
	                } catch (ClassNotFoundException e) {
	                    e.printStackTrace();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
		}
	}
	
	public class ServerListener extends Thread {
		StateManager stateManager;
		public ServerListener(StateManager sm){
			this.stateManager = sm;
			start();
			Thread.yield();
		}
		public void run() {
			try {    
				ServerSocket serverSocket = new ServerSocket(PORT_NO);
				socket = serverSocket.accept();
				loopSender = new LoopSender(stateManager);
				listener = new Listener(stateManager);
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}
