/**
 * Created by nikmal on 2016-12-22.
 */
public class Runner {

    public static void main(String[] args) {

        UI ui = new ConsoleInterface();

        try {
            ui.initialize(args);
            ui.start();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Oh snap, something crashed! Here's the message and stack trace:\n")
                    .append(e.getMessage()).append("\n");

            for (StackTraceElement elem : e.getStackTrace()) {
                sb.append(elem.toString());
            }

            ui.printMessage(sb.toString());
        }
    }
}