/**
 * Created by nikmal on 2016-12-22.
 */
public class Utils {

    enum ProcessMode {
        EXTRACT, DELETE
    }

    public static ProcessMode getProcessMode(String pMode) {
        switch (pMode) {
            case "E":
            case "EXTRACT":
                return ProcessMode.EXTRACT;
            case "D":
            case "DELETE":
                return  ProcessMode.DELETE;
            default:
                return ProcessMode.EXTRACT;
        }
    }
}
