/**
 * Created by nikmal on 2016-12-28.
 */
public abstract class UI {
    protected Extractor extractor;
    protected String dataDirectory = "";
    protected String sentrixIdFile = "";
    protected String dataMappingFile = "";
    protected Utils.ProcessMode processMode;
    protected String dataExtractionId = "";

    public abstract void initialize(String[] args);

    public abstract void start();

    public abstract void printMessage(String msg);
}
