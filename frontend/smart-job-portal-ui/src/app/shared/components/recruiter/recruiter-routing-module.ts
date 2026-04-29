import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { CreateJob } from './create-job/create-job';
import { CandidateList } from './candidate-list/candidate-list';

const routes: Routes = [
  
{ path: '', component: Dashboard },
  { path: 'create-job', component: CreateJob },
  { path: 'jobs/:jobId/candidates', component: CandidateList }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RecruiterRoutingModule { }
