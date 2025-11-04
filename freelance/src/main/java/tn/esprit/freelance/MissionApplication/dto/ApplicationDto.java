package tn.esprit.freelance.MissionApplication.dto;

public class ApplicationDto {
    private String id; // full URI
    private String status; // onto:status
    private String date; // onto:applicationDate ISO String
    private String missionUri; // onto:appliedTo
    private String applicantUri; // onto:submittedBy

    public ApplicationDto() {}

    public ApplicationDto(String id, String status, String date, String missionUri, String applicantUri) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.missionUri = missionUri;
        this.applicantUri = applicantUri;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getMissionUri() { return missionUri; }
    public void setMissionUri(String missionUri) { this.missionUri = missionUri; }
    public String getApplicantUri() { return applicantUri; }
    public void setApplicantUri(String applicantUri) { this.applicantUri = applicantUri; }
}


