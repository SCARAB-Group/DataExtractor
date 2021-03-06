/**
 * Created by nikmal on 2016-12-22.
 */
public class Runner {

    public static void main(String[] args) {

        UI ui = null;

        try {
            String UIMode = "";
            if (args.length > 0) // Assume console UI if there are arguments, else default to GUI.
                UIMode = args[0];

            switch (UIMode) {
                case "C":
                    ui = new ConsoleInterface();
                    ui.initialize(args);
                    ui.start();
                    break;
                case "":
                    ui = new GraphicalInterface();
                    ui.initialize(args);
                    break;
                default:
                    throw new RuntimeException(String.format("Invalid interface option: %s", UIMode));
            }

        } catch (Exception e) { // Catch exceptions and print the message. Ends the program more gently than simply crashing
            StringBuilder sb = new StringBuilder();
            sb.append("Oh snap, something crashed! Here's the message and stack trace:\n")
                    .append(e.getMessage()).append("\n");

            for (StackTraceElement elem : e.getStackTrace()) {
                sb.append(elem.toString());
            }

            // If this happens, something went terribly wrong, just crash.
            if (e.getClass().getName().equals("java.lang.RuntimeException") || ui == null) {
                throw new RuntimeException(sb.toString());
            }
            ui.printMessage(sb.toString());
        }
    }
}