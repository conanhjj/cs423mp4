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
        return Integer.valueOf(properties.getProperty("WorkerThreadCount"));
    }
}
