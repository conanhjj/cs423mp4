package jobs;



import loadbalance.Adaptor;
import util.LBConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorkerThreadManager {
    private List<WorkerThread> threadPool;
    private Integer threadCount;
    private Adaptor adaptor;
    private JobQueue jobQueue;


    public WorkerThreadManager(Adaptor adaptor) {
        threadPool = new ArrayList<WorkerThread>();
        this.adaptor = adaptor;
        jobQueue = new JobQueue();
        init();
    }

    public void init() {
        threadCount = LBConfiguration.getThreadCount();
        for(int i=0;i<threadCount;++i) {
            WorkerThread wt = new WorkerThread(adaptor, this);
            threadPool.add(wt);
        }
    }

    public void start() {
        for(WorkerThread wt : threadPool) {
            wt.start();
        }
    }

    public JobQueue getJobQueue() {
        return jobQueue;
    }

    public Integer getJobQueueSize() {
        return jobQueue.size();
    }

    public void stop() {
        for(WorkerThread wt : threadPool) {
            wt.stop();
        }
    }

    public List<Job> getCurRunningJobs() {
        List<Job> list = new LinkedList<Job>();
        for(WorkerThread wt : threadPool) {
            Job job = wt.getCurRunJob();
            if(job != null) list.add(job);
        }
        return list;
    }
}
