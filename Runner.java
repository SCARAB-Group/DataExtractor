import javax.rmi.CORBA.Util;

/**
 * Created by nikmal on 2016-12-22.
 */
public class Runner {

    public static void main(String[] args) {

        String dataDirectory = "";
        String sentrixIdFile = "";
        String dataMappingFile = "";

        try {
            dataDirectory = args[0];
            sentrixIdFile = args[1];
            dataMappingFile = args[2];
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Missing arguments");
            System.exit(1);
        }

        try {
            Extractor extractor = new Extractor(dataDirectory, sentrixIdFile, dataMappingFile);
            extractor.run(Utils.ProcessMode.EXTRACT);

        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Oh snap, something crashed! Here's the message and stack trace:\n")
                    .append(e.getMessage()).append("\n");

            for (StackTraceElement elem : e.getStackTrace()) {
                sb.append(elem.toString());
            }

            System.out.println(sb.toString());
        }
    }
}