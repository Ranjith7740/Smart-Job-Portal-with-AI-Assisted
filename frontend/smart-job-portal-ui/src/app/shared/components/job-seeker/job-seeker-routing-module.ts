import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { JobList } from './job-list/job-list';
import { JobDetails } from './job-details/job-details';
import { ResumeUpload } from './resume-upload/resume-upload';


const routes: Routes = [
  { path: '', component: JobList },
  { path: 'jobs/:id', component: JobDetails },
  { path: 'resume', component: ResumeUpload },


];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class JobSeekerRoutingModule { }
