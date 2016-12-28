import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by nikmal on 2016-12-22.
 */

public class Extractor {

    private UI ui;
    private String dataDirectory = "";
    private String sentrixIdFile = "";
    private String dataMappingFile = "";
    private String dataExtractionId = "";
    private File outputFolder;
    private HashMap<String, DataItemInfo> referenceList = new HashMap<>(); // Sample barcode is the key value
    private File dataDir;
    private List<File> fileList = new ArrayList<>();

    public Extractor(UI _ui, String _dataDirectory, String _sentrixIdFile, String _dataMappingFile,
                     String _dataExtractionId) {
        ui = _ui;
        dataDirectory = _dataDirectory;
        sentrixIdFile = _sentrixIdFile;
        dataMappingFile = _dataMappingFile;
        dataDir = new File(dataDirectory);
        if (_dataExtractionId != null) {
            dataExtractionId = _dataExtractionId.replaceAll(" ", "_");
        } else {
            LocalDateTime now = LocalDateTime.now();
            dataExtractionId = String.format("%s%s%s%s%s%s", now.getYear(), now.getMonthValue(),
                    now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        }
    }

    public void run(Utils.ProcessMode processMode) {

        ui.printMessage(String.format("Running in mode: %s", processMode));

        // Read and store the data mapping information in memory
        getMappingInfo();

        // Get file name information
        getFileNameInfo();

        //printDataInfoItems(referenceList);

        // Get list of files to go through
        getFileList();

        processFileList(processMode);
    }


    private void getMappingInfo() {
        BufferedReader inDataReader;
        String line;
        String[] lineParts;

        try {
            inDataReader = new BufferedReader(new FileReader(dataMappingFile));

            while ((line = inDataReader.readLine()) != null) {
                try {
                    lineParts = line.split(";");
                    if (lineParts[0].equals("RID")) { continue; }
                    referenceList.put(lineParts[1], new DataItemInfo(Integer.parseInt(lineParts[2]),
                            Integer.parseInt(lineParts[0]), lineParts[1]));
                } catch (Exception e) {
                    printAndAbort(e);
                }
            }
        } catch (IOException e) {
            printAndAbort(e);
        }

        ui.printMessage(String.format("Reference list contains %d participants", referenceList.size()));
    }

    private void getFileNameInfo() {
        BufferedReader inDataReader;
        String line;
        String[] lineParts;
        String linePrefix;

        try {
            inDataReader = new BufferedReader(new FileReader(sentrixIdFile));
            String currentFilename;
            DataItemInfo currentDII;

            while ((line = inDataReader.readLine()) != null) {
                try {
                    linePrefix = line.substring(0, 3);
                    if (!linePrefix.equals("CEP") && !linePrefix.equals("Sam")) {
                        lineParts = line.split("\t");
                        if (lineParts[1].equals("X")) { continue; } // Skip rows where the exclude flag is checked

                        currentFilename = lineParts[0].split("_")[1].replace(".1","");
                        currentDII = referenceList.get(currentFilename);
                        if (currentDII != null) {
                            currentDII.setSentrixSampleId(lineParts[0]);
                            currentDII.setFilenameIdentifier(lineParts[2] + "_" + lineParts[3]);
                        }
                    }
                } catch (Exception e) {
                    printAndAbort(e);
                }
            }
        } catch (IOException e) {
            printAndAbort(e);
        }
    }

    private void getFileList() {
        for (File f : dataDir.listFiles()) {
            if (f.isDirectory()) {
                for (File subf : f.listFiles()) {
                    if (!subf.isDirectory()) {
                        fileList.add(subf);
                    }
                }
            } else
                fileList.add(f);
        }

        ui.printMessage(String.format("Found %d files in given directory", fileList.size()));
    }

    private void processFileList(Utils.ProcessMode mode) {
        int foundCount = 0;
        int notFoundCount = 0;
        DataItemInfo dii;
        outputFolder = new File(dataExtractionId);
        if (!outputFolder.mkdir()) {
            printAndAbort(new Exception(String.format("Unable to create folder %s", dataExtractionId)));
        }

        for (String item : referenceList.keySet()) {
            dii = referenceList.get(item);

            for (File file : fileList) {
                if (file.getName().contains(dii.getFilenameIdentifier())) {
                    handleFile(file, mode);
                    foundCount++;
                } else {
                    notFoundCount++;
                }
            }
        }

        ui.printMessage(String.format("%d files (out of %d) extracted", foundCount, (foundCount + notFoundCount)));
    }

    private void handleFile(File file, Utils.ProcessMode mode) {

        switch (mode) {
            case EXTRACT:
                Path FROM = Paths.get(file.getAbsolutePath());
                Path TO = Paths.get(String.join(File.separator, outputFolder.getAbsolutePath(), file.getName()));
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };
                try {
                    //ui.printMessage(String.format("FROM: %s, TO: %s", FROM.toAbsolutePath(), TO.toAbsolutePath()));
                    java.nio.file.Files.copy(FROM, TO, options);
                } catch (IOException e) {
                    printAndAbort(e);
                }
                break;

            case DELETE:
                // TODO
                break;
        }
    }

    private void printAndAbort(Exception e) {
        ui.printMessage(String.format("Caught exception (%s): %s", e.getClass(), e.getMessage()));
        System.exit(-1);
    }

    private void printDataInfoItems(HashMap<String, DataItemInfo> referenceList) {
        DataItemInfo dii;
        for (String k : referenceList.keySet()) {
            dii = referenceList.get(k);
            ui.printMessage(k + " -> " + dii.toString());
        }

        ui.printMessage(String.format("%d items in total", referenceList.size()));
    }

}
