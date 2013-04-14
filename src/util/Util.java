package util;


public class Util {

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //do nothing
        }
    }
}
