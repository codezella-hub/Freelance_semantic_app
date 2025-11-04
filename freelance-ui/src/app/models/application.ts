export interface ApplicationDto {
  id: string;
  status: string;
  date: string;
  missionUri: string; // onto:appliedTo
  applicantUri: string; // onto:submittedBy
}


