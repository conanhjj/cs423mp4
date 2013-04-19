package jobs;


import loadbalance.Adaptor;
import org.apache.log4j.Logger;
import util.Util;

import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerThread {

    private Thread mainThread;
    private Thread monitorThread;
    private JobQueue jobQueue;
    private Adaptor adaptor;

    private boolean stopWork;
    private AtomicBoolean isSuspended;

    private static Long NO_JOB_SLEEP_INTERVAL = 2000L;
    private static Long FINISH_SLEEP_INTERVAL = 1000L;
    private static Long RUNNING_TIME = 700L;
    private static Long SLEEPING_TIME = 1000L - RUNNING_TIME;

    private static Logger logger = Logger.getLogger(WorkerThread.class);

    private Job curRunJob;

    public WorkerThread(Adaptor adaptor) {
        jobQueue = new JobQueue();
        stopWork = false;
        isSuspended = new AtomicBoolean(false);
        curRunJob = null;
        this.adaptor = adaptor;
    }

    public void start() {
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopWork) {
                    if(jobQueue.isEmpty()) {
                        Util.sleep(NO_JOB_SLEEP_INTERVAL);
                        continue;
                    }

                    if(!isSuspended.get()) {
                        if(curRunJob == null)
                            curRunJob = jobQueue.pop();
                        if(!curRunJob.isFinished())
                            curRunJob.run();

                        if(curRunJob.isFinished() && !curRunJob.hasNotified()) {
                            System.out.println(curRunJob.getResult());
                            curRunJob.setAsNotified();
                            if(adaptor != null)
                                adaptor.jobFinished(curRunJob);
                        }
                    }
                }
            }
        });
        mainThread.start();

        monitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopWork) {
                    if(curRunJob == null) continue;     //TODO: not efficient
                    curRunJob.resume();
                    Util.sleep(RUNNING_TIME);
                    curRunJob.stop();
                    Util.sleep(SLEEPING_TIME);
                }
            }
        });
        monitorThread.start();
    }

    public void stop() {
        stopWork = true;
    }

    /**
     * This method will suspend the worker mainThread. However, it wont' stop running
     * the current job.
     */
    public void suspend() {
        isSuspended.set(true);
    }

    public void resume() {
        isSuspended.set(false);
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

    public JobQueue getJobQueue() {
        return jobQueue;
    }

    public Integer getJobQueueSize() {
        return jobQueue.size();
    }

    public boolean setThrottling(Integer percentage) {
        if(percentage <=0 || percentage >= 100) {
            logger.error("Wrong Throttling Parameter");
            return false;
        }

        RUNNING_TIME = (long) percentage * 10;
        SLEEPING_TIME = 1000L - RUNNING_TIME;
        return true;
    }

    public static void main(String[] args) {
        int[][] matrix = new int[][]{{1,1,1},{1,1,1},{1,1,1}};
        MatrixAdditionJob maj = new MatrixAdditionJob("matrix", 3, 3, 100, matrix);
        WorkerThread wt = new WorkerThread(null);
        wt.addJob(maj);
        wt.start();
        wt.stop();
    }


}
