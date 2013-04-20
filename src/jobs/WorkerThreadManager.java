package jobs;



import loadbalance.Adaptor;
import util.LBConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorkerThreadManager {
    private List<WorkerThread> threadPool;
    private Integer threadCount;
    private Adaptor adaptor;

    public WorkerThreadManager(Adaptor adaptor) {
        threadPool = new ArrayList<WorkerThread>();
        this.adaptor = adaptor;
    }

    public void init() {
        threadCount = LBConfiguration.getThreadCount();
        for(int i=0;i<threadCount;++i) {
            WorkerThread wt = new WorkerThread(adaptor);
            threadPool.add(wt);
        }
    }


}
