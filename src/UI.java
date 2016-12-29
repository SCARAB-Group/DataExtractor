/**
 * Created by nikmal on 2016-12-28.
 */
public abstract class UI {
    Extractor extractor;
    String dataDirectory;
    Utils.ProcessMode processMode;
    String dataExtractionId;
    String participantListFilePath;
    String mappingDataDirectory;

    public abstract void initialize(String[] args);

    public abstract void start();

    public abstract void printMessage(String msg);

    public abstract void displayError(String msg);
}
