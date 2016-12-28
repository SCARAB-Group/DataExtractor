import java.io.*;
import java.util.*;

/**
 * Created by nikmal on 2016-12-22.
 */

public class Extractor {

    private UI ui;
    private String dataDirectory = "";
    private String sentrixIdFile = "";
    private String dataMappingFile = "";
    private HashMap<String, DataItemInfo> referenceList = new HashMap<>(); // Sample barcode is the key value
    private File dataDir;
    private List<File> fileList = new ArrayList<>();
    private int participantsInMappingFile;
    private int filesInGivenDir;
    private int filesExtracted;
    private int filesNotFound;

    public Extractor(UI _ui, String _dataDirectory, String _sentrixIdFile, String _dataMappingFile) {
        ui = _ui;
        dataDirectory = _dataDirectory;
        sentrixIdFile = _sentrixIdFile;
        dataMappingFile = _dataMappingFile;
        dataDir = new File(dataDirectory);
    }

    public void run(Utils.ProcessMode processMode) {

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
                    referenceList.put(lineParts[1], new DataItemInfo(Integer.parseInt(lineParts[2]), Integer.parseInt(lineParts[0]), lineParts[1]));
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

                        //refList.put(lineParts[0].split("_")[1].replace(".1",""), new Pair(Long.parseLong(lineParts[2]), lineParts[3]));
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
        for (String item : referenceList.keySet()) {
            dii = referenceList.get(item);

            for (File file : fileList) {
                if (file.getName().contains(dii.getFilenameIdentifier())) {
                    //System.out.println(String.format("Extract file: %s", file.getName()));

                    // TODO: extract or delete depending on mode
                    handleFile(file, mode);

                    foundCount++;
                } else {
                    //System.out.println(String.format("Failed to find file using identifier: %s", dii.getFilenameIdentifier()));
                    notFoundCount++;
                }
            }
        }

        ui.printMessage(String.format("%d files extracted", foundCount));
        ui.printMessage(String.format("%d files not found", notFoundCount));
    }

    private void handleFile(File file, Utils.ProcessMode mode) {
        switch (mode) {
            case EXTRACT:

                break;

            case DELETE:
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

    public int getParticipantsInMappingFile() { return participantsInMappingFile; }
    public void setParticipantsInMappingFile(int participantsInMappingFile) { this.participantsInMappingFile = participantsInMappingFile; }
    public int getFilesInGivenDir() { return filesInGivenDir; }
    public void setFilesInGivenDir(int filesInGivenDir) { this.filesInGivenDir = filesInGivenDir; }
    public int getFilesExtracted() { return filesExtracted; }
    public void setFilesExtracted(int filesExtracted) { this.filesExtracted = filesExtracted; }
    public int getFilesNotFound() { return filesNotFound; }
    public void setFilesNotFound(int filesNotFound) { this.filesNotFound = filesNotFound; }
}
