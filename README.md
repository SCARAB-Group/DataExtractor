# DataExtractor
Find and output Illumina genotype data from a set of id's.

##Preparations
The program needs to have two files containing mapping information from MyResearch and the genotyping facility. Check the file README.txt inside the folder "Mappingdata" in this project for details.<br><br>
You also need to provide an ID for the data extraction and a list of participants (ParticipantId) for whom to make the extraction (more on this below, depending on the mode of execution).

##Run the application as either a console application or a window application.

###As a console application:
1. Download the source code and build a JAR file, or download the one available in this repository.<br>
2. Open a terminal and start the application using the command:<br>
```
java -jar DataExtractor.jar C <MODE> [DATA_EXTRACTION_ID] [GENOTYPE_DATA_DIR] [MAPPING_DATA_DIR] [PATH_TO_LIST_OF_PARTICIPANT_IDS]
```
[MODE] = E or D (Extract or Delete).  
[DATA_EXTRACTION_ID] = Any string that does not contain invalid characters for a folder name.<br>
[GENOTYPE_DATA_DIR] = Directory containing the genotype data (subfolders are okay, but only one level).<br>
[MAPPING_DATA_DIR] = Directory containing the mapping data (see "Preparations" above).<br>
[PATH_TO_LIST_OF_PARTICIPANT_IDS] = Path to a file contaning Participant Ids, one Id per row, plain text file.<br>
<br>
For example: java -jar DataExtractor.jar C E C:/MyFolder/GenotypeData C:/OtherFolder/Mappingdata C:/Docs/listofparticipants.txt<br><br>
3. Wait a few moments and the program will copy the files beloning to the participants in the supplied file to a folder name. This folder will appear in the same directory as the JAR file<br>

###As a window application:
1. Double-click DataExtractor.jar.
2. Fill in all the required parameters. No dataextraction Id is required in DELETE mode.
3. Click Go and the program will copy the files beloning to the participants in the supplied file to a folder name. This folder will appear in the same directory as the JAR file<br>

