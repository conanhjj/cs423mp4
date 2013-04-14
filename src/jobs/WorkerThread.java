package jobs;


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

    private static Long NO_JOB_SLEEP_INTERVAL = 2000L;
    private static Long FINISH_SLEEP_INTERVAL = 1000L;

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
                        Util.sleep(NO_JOB_SLEEP_INTERVAL);
                        continue;
                    }

                    Job job = jobQueue.pop();
                    executeJob(job);
                    Util.sleep(FINISH_SLEEP_INTERVAL);
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

    private void executeJob(String command) {
        try {
            List<String> cmds = new LinkedList<String>();
            cmds.add("sh");
            cmds.add("-c");
            cmds.add(command);

            ProcessBuilder pb = new ProcessBuilder(cmds);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));

            String lineStr;

            while((lineStr = br.readLine()) != null) {
                System.out.println(lineStr);
            }

            if(p.waitFor() != 0) {
                if(p.exitValue() == 1) {
                    System.err.println("Job Execute error");
                }
            }

        } catch (IOException ex) {
            System.err.println(ex);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
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