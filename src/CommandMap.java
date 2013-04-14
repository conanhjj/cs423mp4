import java.util.HashMap;
import java.util.Map;

public class CommandMap {

    private static final String CMD_HELP = "help";
    private static final String CMD_CONNECT = "connect";

    private static Map<String, String> funcMap = new HashMap<String, String>();

    public static void init() {
        funcMap.put(CMD_HELP, "printHelp");
        funcMap.put(CMD_CONNECT, "connectNode");
    }

    public static String getFuncName(String str) {
        return funcMap.get(str);
    }

    public static Integer size() {
        return funcMap.size();
    }

    public static Map<String, String> getMap() {
        return funcMap;
    }
}


