package jobs;


import org.apache.log4j.Logger;
import util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class WorkerThread {

    private Thread thread;
    private JobQueue jobQueue;

    private boolean stopWork;
    private boolean suspend;

    private static Long NO_JOB_SLEEP_INTERVAL = 2000L;
    private static Long FINISH_SLEEP_INTERVAL = 1000L;
    private static Long RUNNING_TIME = 700L;
    private static Long SLEEPING_TIME = 1000L - RUNNING_TIME;

    private static Logger logger = Logger.getLogger(WorkerThread.class);

    public WorkerThread() {
        jobQueue = new JobQueue();
        stopWork = false;
        suspend = false;
    }

    public void start() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopWork) {
                    if(jobQueue.isEmpty()) {
                        Util.sleep(NO_JOB_SLEEP_INTERVAL);
                        continue;
                    }

                    if(!suspend) {
                        Job job = jobQueue.pop();
                        executeJob(job, thread);
                    }
                    //Util.sleep(FINISH_SLEEP_INTERVAL);
                }
            }
        });
        thread.start();
    }

    public void stop() {
        stopWork = true;
    }

    /**
     * This method will suspend the worker thread. However, it wont' stop running
     * the current job.
     */
    public void suspend() {
        synchronized (this) {
            suspend = true;
        }
    }

    public void resume() {
        synchronized (this) {
            suspend = false;
        }
    }

    public void executeJob(Job job, Thread curThread) {
        //TODO: fake code
        String result = execute(job.getExecuteName(), curThread);
        if(result != null) {
            job.setJobResult(result);
        } else {
            System.out.println("There is no result for job");
        }
        Util.sleep(2000);
    }

    private static String execute(String command, final Thread curThread) {
        List<String> cmds = new LinkedList<String>();
//            cmds.add("sh");
//            cmds.add("-c");
        cmds.add("./" + command);

        System.out.println(cmds.toString());

        ProcessBuilder pb = new ProcessBuilder(cmds);
        Process p;
        BufferedReader br;
        try {
            p = pb.start();
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
        } catch (IOException ex) {
            logger.info("Create Running Process", ex);
            return null;
        }

        String lineStr, result;
        result = null;
        while(true) {
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Util.sleep(RUNNING_TIME);
                        curThread.interrupt();
                    }
                }).start();

                Integer retVal = p.waitFor();
                if(retVal != 0) {
                    logger.error("Job Execute Error");
                    return null;
                } else {
                    break;
                }
            }  catch (InterruptedException ex) {
//                System.out.println("WorkerThread sleeping");
                Util.sleep(SLEEPING_TIME);
            }
        }

        try {
            while((lineStr = br.readLine()) != null) {
                if(result == null) {
                    result = lineStr;
                } else {
                    result += "\n" + lineStr;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);
        return result;
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
        Job job = new Job("test2");
        job.loadJobFromFile();
        job.saveJobToFile();


        execute(job.getExecuteName(), Thread.currentThread());
    }
}
