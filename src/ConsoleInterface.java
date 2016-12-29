/**
 * Created by nikmal on 2016-12-28.
 */
public class ConsoleInterface extends UI {

    public ConsoleInterface() {}

    @Override
    public void initialize(String[] args) {
        try {
            processMode = Utils.getProcessMode(args[1]);
            dataExtractionId = args[2];
            dataDirectory = args[3];
            mappingDataDirectory = args[4];
            participantListFilePath = args[5];
        } catch (ArrayIndexOutOfBoundsException ex) {
            printMessage("Missing arguments");
            System.exit(1);
        }

        this.extractor = new Extractor(this, dataExtractionId, mappingDataDirectory, dataDirectory,
                participantListFilePath);
    }

    @Override
    public void start() {
        extractor.run(processMode);
    }

    @Override
    public void printMessage(String msg) {
        System.out.println(msg);
    }

    @Override
    public void displayError(String msg) {
        printMessage(msg);
    }
}
