export interface ResumeModel {
  id: number;
  filePath: string;
  score?: number;
  feedback?: string;
  matchedSkills?: string;
  missingSkills?: string;
  active: boolean;
}