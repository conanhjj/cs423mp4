package jobs;


import util.Util;

public class WorkerThread {

    private Thread thread;
    private JobQueue jobQueue;

    private boolean stopWork;

    private static Long SLEEP_INTERVAL = 2000L;

    public WorkerThread() {
        jobQueue = new JobQueue();
        stopWork = false;
    }

    public void run() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopWork) {
                    if(jobQueue.isEmpty()) {
                        Util.sleep(SLEEP_INTERVAL);
                        continue;
                    }

                    Job job = jobQueue.pop();
                    executeJob(job);
                }
            }
        });
    }

    public void stop() {
        synchronized (this) {
            stopWork = true;
        }
    }

    public void executeJob(Job job) {
        //TODO: fake code
        Util.sleep(2000);
    }

    public void addJob(Job job) {
        synchronized (this) {
            jobQueue.append(job);
        }
    }

    public boolean isEmpty() {
        synchronized (this) {
            return jobQueue.isEmpty();
        }
    }
}
