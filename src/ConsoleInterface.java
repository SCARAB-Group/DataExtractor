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
            dataExtractionId = args[2];
            participantListFilePath = args[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            printMessage("Missing arguments");
            System.exit(1);
        }

        this.extractor = new Extractor(this, dataDirectory, dataExtractionId, participantListFilePath);
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
