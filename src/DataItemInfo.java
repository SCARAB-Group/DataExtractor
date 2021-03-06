/**
 * Created by nikmal on 2016-12-22.
 */
public class DataItemInfo {

    private Integer participantId;
    private Integer RID;
    private String sampleBarcode;
    private String sentrixSampleId;
    private String filenameIdentifier;

    public Integer getParticipantId() { return participantId; }
    public Integer getRID() { return RID; }
    public String getSampleBarcode() { return sampleBarcode; }
    public String getFilenameIdentifier() { return filenameIdentifier; }
    public void setFilenameIdentifier(String filenameIdentifier) { this.filenameIdentifier = filenameIdentifier; }
    public String getSentrixSampleId() { return sentrixSampleId; }
    public void setSentrixSampleId(String sentrixSampleId) { this.sentrixSampleId = sentrixSampleId; }

    DataItemInfo(Integer _participantId, Integer _RID, String _sampleBarcode) {
        participantId = _participantId;
        RID = _RID;
        sampleBarcode = _sampleBarcode;
    }

    public String toString() {
        return String.join(" | ", new String[] {
            getParticipantId().toString(), getRID().toString(), getSampleBarcode(), getFilenameIdentifier(), getSentrixSampleId()
        });
    }
}
