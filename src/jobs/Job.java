package jobs;


import java.io.Serializable;

public class Job implements Serializable {

    private JobResult result;
    private String executeCommand;

    public Job() {
        this("null");
    }

    public Job(String cmd) {
        result = new JobResult();
        executeCommand = cmd;
    }

    public String getCommand() {
        return executeCommand;
    }
}
