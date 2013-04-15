package jobs;

import java.io.Serializable;

public class JobResult implements Serializable {

    private String result;

    private static final long serialVersionUID = 6234566222960044811L;

    public JobResult() {

    }

    public void setResult(String result) {
        this.result = result;
    }

}
