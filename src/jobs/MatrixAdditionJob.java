package jobs;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MatrixAdditionJob extends Job {
    private Integer row;
    private Integer column;
    private AtomicInteger count;
    private Integer result;
    private AtomicBoolean interrupt;

    private int[][] matrix;

    public MatrixAdditionJob(String fileName, Integer row, Integer column, int count, int[][] matrix) {
        super(fileName);
        this.row = row;
        this.column = column;
        this.matrix = new int[row][column];
        for(int i=0;i<row;++i) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, column);
        }
        result = 0;
        this.count = new AtomicInteger(count);
        interrupt = new AtomicBoolean(false);
    }

    public MatrixAdditionJob(boolean isRequest) {
        super(isRequest);
    }

    @Override
    public void run() {
        while(count.get()>0 && !interrupt.get()) {
            count.decrementAndGet();
            for(int i=0;i<row;++i) {
                for(int j=0;j<column;++j)
                    result += matrix[i][j];
            }
        }
    }

    @Override
    public boolean isFinished() {
        synchronized (this) {
            return count.get() == 0;
        }
    }

    @Override
    public void stop() {
        interrupt.set(true);
    }

    @Override
    public void resume() {
        interrupt.set(false);
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public JobResult getResult() {
        return new MatrixAdditionResult(result);
    }
}
