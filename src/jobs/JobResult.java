package jobs;

import java.io.Serializable;

public interface JobResult {

    public String getResult();

    public JobResult aggregate(JobResult result);
}
