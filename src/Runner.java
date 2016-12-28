/**
 * Created by nikmal on 2016-12-22.
 */
public class Runner {

    public static void main(String[] args) {

        try {

            UI ui;
            String UIMode = args[0];

            switch (UIMode) {
                case "C":
                    ui = new ConsoleInterface();
                    break;
                //case "":
                    //TODO: assign a GUI
                //    break;
                default:
                    throw new RuntimeException(String.format("Invalid interface option: %s", UIMode));
            }

            ui.initialize(args);
            ui.start();
        } catch (Exception e) {

            StringBuilder sb = new StringBuilder();
            sb.append("Oh snap, something crashed! Here's the message and stack trace:\n")
                    .append(e.getMessage()).append("\n");

            for (StackTraceElement elem : e.getStackTrace()) {
                sb.append(elem.toString());
            }

            throw new RuntimeException(sb.toString());
        }
    }
}