package state;

import java.io.*;
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
	public boolean isRunning;
	public boolean isPeriodic;
	private LoopSender loopSender;
	private Listener listener;
	private int PORT_NO;
	public int n_state_transfer = 0;
	
	public StateManager(int interval, int serverPort, boolean isPeriodic) {
		this.interval = interval;
		PORT_NO = serverPort;
		this.isPeriodic = isPeriodic;
		setState(new State(-1, -1, -1));
		new ServerListener(this);
	}
	
	public StateManager(int serverPort, boolean isPeriodic){
		this(5000, serverPort, isPeriodic);
	}
	
	public StateManager(int serverPort){
		this(serverPort, true);
	}
	
	public void init(){
		isRunning = true;
		loopSender = new LoopSender(this);
		listener = new Listener(this);
	}
	
	public void tryConnect(String hostname, int port){
		try {
			socket = new Socket(hostname, port);
			init();
			System.out.println("init stateManager in tryConnect");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void setState(State state){
		this.state = new State(state);
	}
	
	private synchronized void setRemoteState(State state){
		//System.out.println("Obtain remote state: " + state);
		this.remoteState = new State(state);
	}
	
	public synchronized void sendState(){
		n_state_transfer++;
		try 
        {
			//System.out.println("send current state: " + state);
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(state);
            objectOutput.flush();
        } 
        catch (IOException e) 
        {
            //e.printStackTrace();
        } 
	}
	
	public synchronized State getRemoteState(){
		return new State(this.remoteState);
	}
	
	public class LoopSender extends Thread {
		StateManager stateManager;
		public LoopSender(StateManager sm) {
			this.stateManager = sm;
			
		    start();
		    Thread.yield();
		}

		public void run() {
			while(isRunning && isPeriodic)	{
				stateManager.sendState();
				try {
					sleep(stateManager.interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
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
			while(isRunning)	{
				// read message from socket;
				try {
					ObjectInputStream objectInput = new ObjectInputStream(stateManager.socket.getInputStream());
	                try {
	                    state.State state = (state.State) objectInput.readObject();
	                    n_state_transfer++;
	                    stateManager.setRemoteState(state);
	                } catch (ClassNotFoundException e) {
	                    e.printStackTrace();
	                }
				} catch (IOException e) {
					System.out.println("State Manager's socket: Disconnected");
					break;
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
				init();
				System.out.println("init stateManager in ServerListener");
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}
