In this directory you need to put mapping files in order for DataExtractor to work:

"Datamapping.csv"
Must contain the following columns:
- RID (referral id)
- Sample (sample barcode)
- ParticipantId

"*SentrixIDs.txt"
These files need to be placed in a folder called "SentrixIDs" (in this folder) and must be in the format given by the sequencing lab (this should be standard?).
The following columns are expected:
- Sample ID
- att exkludera
- Array Info.Sentrix ID
- Array Info.Sentrix Position