import javafx.util.Pair;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.util.*;

/**
 * Created by nikmal on 2016-12-22.
 */

public class Extractor {

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

        File dataDir = new File(dataDirectory);
        List<File> fileList = new ArrayList<>();
        HashMap<String, Pair<Long, String>> refList = new HashMap<>();
        List<Integer> participantList = new ArrayList<>(); // this should be an input
        HashMap<String, DataItemInfo> referenceList = new HashMap<>(); // Sample barcode is the key value

        BufferedReader inDataReader;
        String linePrefix;
        String line;
        String[] lineParts;

        // Read and store the data mapping information in memory
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

        /*
        DataItemInfo d;
        for (String key : referenceList.keySet()) {
            d = referenceList.get(key);
            System.out.println(String.join(" | ", new String[] {d.getSampleBarcode(), d.getParticipantId().toString(), d.getRID().toString() }));
        }
        */

        // Get file name information
        try {
            inDataReader = new BufferedReader(new FileReader(sentrixIdFile));
            String currentFilename;
            DataItemInfo currentDII;

            while ((line = inDataReader.readLine()) != null) {
                try {
                    linePrefix = line.substring(0, 3);
                    if (!linePrefix.equals("CEP") || !linePrefix.equals("Sam")) {
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
                    System.out.println("Exception: " + e.getClass() + " :: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            printAndAbort(e);
        }

        printDataInfoItems(referenceList);

        // Build file list
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

        System.out.println(String.format("Found %d files", fileList.size()));

        /*
        for (File f : fileList) {
            System.out.println(f.getName());
        }
        */

        int foundCount = 0;
        int notFoundCount = 0;
        DataItemInfo dii;
        for (String item : referenceList.keySet()) {
            dii = referenceList.get(item);

            for (File file : fileList) {
                if (file.getName().contains(dii.getFilenameIdentifier())) {
                    //System.out.println(String.format("Extract file: %s", file.getName()));
                    foundCount++;
                } else {
                    //System.out.println(String.format("Failed to find file using identifier: %s", dii.getFilenameIdentifier()));
                    notFoundCount++;
                }
            }
        }

        System.out.println(String.format("%d files extracted", foundCount));
        System.out.println(String.format("%d files not found", notFoundCount));

    }

    static void printAndAbort(Exception e) {
        System.out.println(String.format("Caught exception (%s): %s", e.getClass(), e.getMessage()));
        System.exit(-1);
    }

    static void printDataInfoItems(HashMap<String, DataItemInfo> referenceList) {
        DataItemInfo dii;
        for (String k : referenceList.keySet()) {
            dii = referenceList.get(k);
            System.out.println(k + " -> " + dii.toString());
        }

        System.out.println(String.format("%d items in total", referenceList.size()));
    }
}
