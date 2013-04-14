package jobs;


import java.io.Serializable;
import java.util.UUID;

public class Job implements Serializable {

    private JobResult result;
    private String executeCommand;

    private String fileName;
    private String binaryCode;

    private UUID jobId;

    public Job() {
        this("null");
    }

    public Job(String fileName) {
        this.fileName = fileName;
        jobId = UUID.randomUUID();
    }

    public String getCommand() {
        return executeCommand;
    }
}
