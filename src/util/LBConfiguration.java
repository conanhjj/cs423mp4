package util;


import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LBConfiguration {

    private static final String FILE_NAME = "LoadBalancer.properties";
    private static Logger logger = Logger.getLogger(LBConfiguration.class);

    private static Properties properties;

    public static boolean init() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(FILE_NAME));
        } catch (IOException e) {
            logger.error("Couldn't load configuration file");
            return false;
        }
        return true;
    }

    public static Integer getThreadCount() {
        String val = properties.getProperty("WorkerThreadCount");
        if(val == null) {
            logger.error("Couldn't find property WorkerThreadCount, using default value 1");
            return 1;
        } else
            return Integer.valueOf(val);
    }

    public static String RECEIVER = "Receiver";
    public static String SENDER = "Sender";
    public static String SYMMETRIC = "Symmetric";

    public static String getTransferPolicy() {
        String val = properties.getProperty("TransferPolicy");
        if(val == null) {
            logger.error("Couldn't find TransferPolicy, using Receiver instead");
            return RECEIVER;
        }
        if(val.equals(RECEIVER)) {
            return RECEIVER;
        } else if(val.equals(SENDER)) {
            return SENDER;
        } else if(val.equals(SYMMETRIC)) {
            return SYMMETRIC;
        } else {
            logger.error("Wrong value for TransferPolicy, using Receiver instead");
            return RECEIVER;
        }
    }

    public static String EVENT = "Event";
    public static String PERIODIC = "Periodic";

    public static String getInformationPolicy() {
        String val = properties.getProperty("InformationPolicy");
        if(val == null) {
            logger.error("Couldn't find InformationPolicy, using Event instead");
            return EVENT;
        }

        if(val.equals(EVENT))
            return EVENT;
        else if(val.equals(PERIODIC))
            return PERIODIC;
        else {
            logger.error("Wrong value for InformationPolicy, using Event instead");
            return EVENT;
        }
    }
}
