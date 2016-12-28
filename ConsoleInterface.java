/**
 * Created by nikmal on 2016-12-28.
 */
public class ConsoleInterface extends UI {

    public ConsoleInterface() {}

    @Override
    public void initialize(String[] args) {
        try {
            processMode = Utils.getProcessMode(args[0]);
            dataDirectory = args[1];
            sentrixIdFile = args[2];
            dataMappingFile = args[3];
            dataExtractionId = args[4];
        } catch (ArrayIndexOutOfBoundsException ex) {
            printMessage("Missing arguments");
            System.exit(1);
        }

        this.extractor = new Extractor(this, dataDirectory, sentrixIdFile, dataMappingFile, dataExtractionId);
    }

    @Override
    public void start() {
        extractor.run(processMode);
    }

    @Override
    public void printMessage(String msg) {
        System.out.println(msg);
    }
}
