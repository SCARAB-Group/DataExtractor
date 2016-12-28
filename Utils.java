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
                return ProcessMode.EXTRACT;
            case "D":
                return  ProcessMode.DELETE;
            default:
                return ProcessMode.EXTRACT;
        }
    }
}
