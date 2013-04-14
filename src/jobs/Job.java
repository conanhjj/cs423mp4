package jobs;


import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class Job implements Serializable {

    public byte version = 100;

    private JobResult result;

    private String fileName;
    private byte[] binaryCode;
    private Integer jobSize;

    private static Integer MAX_JOB_SIZE = 65536;

    private UUID jobId;



    public Job() {
        this("null");
    }

    public Job(String fileName) {
        this.fileName = fileName;
        jobId = UUID.randomUUID();

        binaryCode = new byte[MAX_JOB_SIZE];
    }

    public String getExecuteName() {
        return fileName + "_" + jobId.toString();
    }

    public boolean loadJobFromFile() {
        DataInputStream dis;

        try {
            dis = new DataInputStream(new FileInputStream(fileName));

            jobSize = dis.read(binaryCode);
            if(jobSize.equals(MAX_JOB_SIZE)) {
                System.err.println("Job too large! Exceeds 64k");
                dis.close();
                return false;
            }
            dis.close();
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public boolean saveJobToFile() {
        File file = new File(getExecuteName());
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new FileOutputStream(file));
            dos.write(binaryCode, 0, jobSize);
            dos.close();

            if(!file.setExecutable(true)) {
                System.err.println("Setting executing permission for job file failed");
                return false;
            }
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * For testing
     * @param args
     */
    public static void main(String[] args) throws Exception{
        Job job = new Job("test");
        job.loadJobFromFile();
        job.saveJobToFile();
    }
}
