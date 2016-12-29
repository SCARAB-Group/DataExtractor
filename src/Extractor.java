import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by nikmal on 2016-12-22.
 */

class Extractor {

    private UI ui;
    private final String MAPPING_FILE = "Datamapping.csv";
    private final String SENTRIX_ID_FOLDER = "SentrixIDs";
    private String dataExtractionId = "";
    private File outputFolder;
    private File dataDir;
    private File mappingDataDir;
    private HashMap<String, DataItemInfo> referenceList = new HashMap<>(); // Sample barcode is the key value
    private List<File> fileList = new ArrayList<>();
    private List<Integer> participantIds;

    Extractor(UI _ui, String _dataExtractionId, String _mappingDataDir, String _dataDirectory,
              String _participantListFilePath) {
        ui = _ui;
        if (_dataExtractionId != null) {
            dataExtractionId = _dataExtractionId.replaceAll(" ", "_");
        } else {
            LocalDateTime now = LocalDateTime.now();
            dataExtractionId = String.format("%s%s%s%s%s%s", now.getYear(), now.getMonthValue(),
                    now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        }
        dataDir = new File(_dataDirectory);
        participantIds = createparticipandIdList(_participantListFilePath);
        mappingDataDir = new File(_mappingDataDir);
    }

    void run(Utils.ProcessMode processMode) {

        ui.printMessage(String.format("Running in mode: %s", processMode));

        /**
            The methods getMappingInfo() and getFileNameInfo() read the information in the files located in the mapping
            data folder. The information is put together in a HashMap that is used to translate a participant Id to a
            file using the sample barcode as the common denominator.
         */

        // I. Read and store the data mapping information in memory.
        getMappingInfo();

        // II. Get file name information.
        getFileNameInfo();

        // III. Get list of files to go through.
        getFileList();

        // IV. Do stuff depending on the process mode given by the user.
        processFileList(processMode);
    }

    private void getMappingInfo() {
        BufferedReader inDataReader;
        String line;
        String[] lineParts;

        try {
            inDataReader = new BufferedReader(new FileReader(String.join(File.separator, mappingDataDir.getAbsolutePath(), MAPPING_FILE)));

            while ((line = inDataReader.readLine()) != null) {
                /**
                    File format:
                    Column 0: RID
                    Column 1: SampleId/barcode
                    Column 2: ParticipantId
                 */
                try {
                    lineParts = line.split(";");
                    if (lineParts[0].equals("RID")) { continue; }

                    if (participantIds.contains(Integer.parseInt(lineParts[2]))) {
                        referenceList.put(lineParts[1], new DataItemInfo(Integer.parseInt(lineParts[2]),
                                Integer.parseInt(lineParts[0]), lineParts[1]));
                    }

                } catch (Exception e) {
                    printAndAbort(e);
                }
            }

        } catch (IOException e) {
            printAndAbort(e);
        }
    }

    private void getFileNameInfo() {
        BufferedReader inDataReader;
        String line;
        String[] lineParts;
        String linePrefix;

        /**
         *  File format:
         *  Column 0: RID
         *  Column 1: exclude flag ("att exkludera")
         *  Column 2: Sentrix ID (ID of the plate that held the samples)
         *  Column 3: Sentrix position (ID of the position of the plate)
         */

        try {
            File sentrixDir = new File(String.join(File.separator, mappingDataDir.getAbsolutePath(), SENTRIX_ID_FOLDER));

            for (File sf : sentrixDir.listFiles()) {
                inDataReader = new BufferedReader(new FileReader(String.join(File.separator, sentrixDir.getAbsolutePath(), sf.getName())));
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
        DataItemInfo dii;

        if (mode.equals(Utils.ProcessMode.EXTRACT)) {
            outputFolder = new File(dataExtractionId);
            if (!outputFolder.mkdir()) {
                printAndAbort(new Exception(String.format("Unable to create folder %s", dataExtractionId)));
            }
        }

        for (String item : referenceList.keySet()) {
            dii = referenceList.get(item);

            for (File file : fileList) {
                if (file.getName().contains(dii.getFilenameIdentifier())) {
                    handleFile(file, mode);
                    foundCount++;
                }
            }
        }

        ui.printMessage(String.format("%d files processed", foundCount));
        ui.printMessage(String.format("Reference list contains %d participants", referenceList.size()));
    }

    private void handleFile(File file, Utils.ProcessMode mode) {

        switch (mode) {
            case EXTRACT:
                Path FROM = Paths.get(file.getAbsolutePath());
                Path TO = Paths.get(String.join(File.separator, outputFolder.getAbsolutePath(), file.getName()));
                CopyOption[] options = new CopyOption[] {
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };
                try {
                    java.nio.file.Files.copy(FROM, TO, options);
                    ui.printMessage(String.format("File copied: %s", file.getName()));
                } catch (IOException e) {
                    printAndAbort(e);
                }
                break;

            case DELETE:
                if (file.delete()) {
                    ui.printMessage(String.format("File deleted: %s", file.getName()));
                } else {
                    ui.printMessage(String.format("Failed to delete file: %s", file.getName()));
                }
                break;
        }
    }

    private List<Integer> createparticipandIdList(String filePath) {
        List<Integer> ids = new ArrayList<>();
        try {
            BufferedReader participandListReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = participandListReader.readLine()) != null) {
                ids.add(Integer.parseInt(line));
            }
        } catch (Exception e) {
            printAndAbort(e);
        }

        return ids;
    }

    private void printAndAbort(Exception e) {
        ui.displayError(String.format("Caught exception (%s): %s", e.getClass(), e.getMessage()));
        System.exit(1);
    }
}
