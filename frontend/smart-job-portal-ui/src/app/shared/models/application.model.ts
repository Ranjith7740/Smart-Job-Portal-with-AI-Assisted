import {  JobModel } from './job.model';
import { ResumeModel } from './resume.model';

export interface ApplicationModel {
  id: number;
  status: string;
  appliedDate: string;
  job: JobModel;
  user: {
    id: number;
    name: string;
    email: string;
    resumes?: ResumeModel[];
  };

  
}