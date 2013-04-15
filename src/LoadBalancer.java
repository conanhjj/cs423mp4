import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import util.Util;

import javax.activation.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Scanner;

import loadbalance.Adaptor;

public class LoadBalancer {

    private static Logger logger = Logger.getLogger(LoadBalancer.class);
    private static Scanner in = new Scanner(System.in);
    private static Adaptor adaptor;
    private static String[] parameters;

    public static void main(String[] args) {
        log4jConfigure();
        init();
        work();
    }

    private static void work() {

        printWelcomeMessage();

        while(true) {
            printCommandNotifier();
            String cmd = in.nextLine();
            parameters = cmd.split(" ");
            if(parameters[0].toUpperCase().equals("QUIT")) {
                break;
            }

            String funcName = CommandMap.getFuncName(parameters[0]);
            if(funcName == null)
                System.out.println("Wrong command name");
            else
                Util.callStaticMethod(LoadBalancer.class, funcName);
        }

        printGoodbyeMessage();
    }

    private static final String FORMAT_STRING = "%-25s%-25s\n";
    public static void printHelp() {
        System.out.printf(FORMAT_STRING, "COMMAND", "USAGE");
        System.out.printf(FORMAT_STRING, "connect <IP>:<PORT>", "connect the remote node");
        System.out.printf(FORMAT_STRING, "start <PORT>", "start the node using given port");
        System.out.printf(FORMAT_STRING, "quit", "quit the program");
    }

    public static void connectNode() {
        if(parameters.length != 2) {
            System.out.println("Wrong command format. Should use connect <IP>:<PORT>, eg connect 127.0.0.1:20000");
            return;
        }
        String param = parameters[1];
        //TODO: WRITE YOUR CONNECT CODE HERE
        int delim = param.indexOf(':');
        String hostname = param.substring(0, delim);
        int ip = Integer.parseInt(param.substring(delim + 1));
        adaptor.tryConnect(hostname, ip);
    }

    public static void startNode() {
        if(parameters.length != 2) {
            System.out.println("Wrong command format. Should use start <PORT>, eg start 20000");
            return;
        }

        Integer port = Integer.valueOf(parameters[1]);
        adaptor = new Adaptor(port);
    }

    private static void printWelcomeMessage() {
        System.out.println("Welcome to use LoadBalancer!");
        System.out.println("Author: Raviphol Sukhajoti, Junjie Hu");
    }

    private static void printGoodbyeMessage() {
        System.out.println("Thanks for using. Goodbye!");
        System.out.println();
        System.out.println();
    }

    private static void printCommandNotifier() {
        System.out.flush();
        System.out.print(">");
    }

    private static void init() {

        CommandMap.init();
        logger.info("Load "+ CommandMap.size() + " command(s) successfully");

        //TODO: put all your init code here
        logger.info("Program initializes successfully");
        System.out.println("--------------------------------------------");
    }

    private static void log4jConfigure() {
        PropertyConfigurator.configure("log4j.properties");
        System.out.println("Configure log4j successfully");
    }
}
