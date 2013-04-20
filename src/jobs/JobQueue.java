package jobs;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import loadbalance.Adaptor;

public class JobQueue implements Iterable<Job>{
	private final int NEWEST = -1;
	private final int OLDEST = -2;
    private List<Job> jobList;
    private Adaptor adaptor;

    public JobQueue(Adaptor adaptor) {
    	this.adaptor = adaptor;
        jobList = new LinkedList<Job>();
    }

    public void append(Job job) {
        synchronized (this) {
            jobList.add(job);
            adaptor.queueSizeChange();
        }
    }

    public Job peek() {
        synchronized (this) {
            if(isEmpty())
                return null;
            else
                return jobList.get(0);
        }
    }

    public Job pop() {
        synchronized (this) {
            if(isEmpty())
                return null;
            else{
                Job temp = jobList.remove(0);
                adaptor.queueSizeChange();
                return temp;
            }
        }
    }
    
    public Job popIfLengthExceed(int THRESHOLD, int index){
    	synchronized (this) {
    		if(jobList.size() > THRESHOLD){
    			index = (index == OLDEST) ? 0 : (index == NEWEST) ? (jobList.size() -1) : index;
    			Job temp = jobList.remove(index);
    			adaptor.queueSizeChange();
    			return temp;
    		}
    	}
    	return null;
    }

    public boolean isEmpty() {
        synchronized (this) {
            return jobList.isEmpty();
        }
    }

    public Integer size() {
        synchronized (this) {
            return jobList.size();
        }
    }

    /*
    public static void main(String[] args) {
        JobQueue jobQueue = new JobQueue();
        Job job = jobQueue.pop();
        System.out.println(job);
    }
    */

    @Override
    public Iterator<Job> iterator() {
        synchronized (this) {
            List<Job> newList = new LinkedList<Job>(jobList);
            return newList.iterator();
        }
    }
}
