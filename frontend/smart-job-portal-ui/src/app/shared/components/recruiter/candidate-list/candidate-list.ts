import { Component, OnInit } from '@angular/core';
import { JobModel } from '../../../models/job.model';
import { ApplicationModel } from '../../../models/application.model';
import { ActivatedRoute } from '@angular/router';
import { Application } from '../../../../core/services/application';
import { Job } from '../../../../core/services/job';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { RouterModule } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { ResumeModel } from '../../../models/resume.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-candidate-list',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatPaginatorModule,
    RouterModule,
    MatSelectModule,
    MatInputModule,      // Add this here
    ReactiveFormsModule


  ],
  templateUrl: './candidate-list.html',
  styleUrl: './candidate-list.scss',
})
export class CandidateList implements OnInit {
  
  
  filterForm: any;
  jobId!: number;
  job!: JobModel;
  applications: ApplicationModel[] = [];
  loading = false;
  error = '';
  // Match these exactly with your Backend Enum (ApplicationStatus)
  statuses: string[] = ['APPLIED', 'SCREENING', 'INTERVIEW', 'OFFER', 'HIRED', 'REJECTED'];
  constructor(
    private route: ActivatedRoute,
    private applicationService: Application,
    private jobService: Job,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.filterForm = this.fb.group({
    status: [''],
    minScore: [''],
    skill: ['']
  });
  }

  ngOnInit(): void {
  // Capture the ID and assign it to the class property (this.jobId)
  this.jobId = Number(this.route.snapshot.paramMap.get('jobId'));
  
  if (this.jobId) {
    this.loadJob(this.jobId);
    this.loadCandidates(this.jobId);
  } else {
    this.error = "No Job ID provided in the URL";
  }
}

  loadJob(jobId: number) {
    this.jobService.getJobById(jobId).subscribe({
      next: job => this.job = job as JobModel,
      error: () => this.error = 'Failed to load job'
    });
  }

  loadCandidates(jobId: number) {
    this.loading = true;

    this.applicationService.getJobApplications(jobId).subscribe({
      next: apps => {
        this.applications = apps as ApplicationModel[];
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load candidates';
        this.loading = false;
      }
    });
  }

  applyFilters() {
  const { status, minScore, skill } = this.filterForm.value;

  this.loading = true;

  this.applicationService.filterCandidates(
    this.jobId,
    status || undefined,
    minScore ? Number(minScore) : undefined,
    skill || undefined
  ).subscribe({
    next: apps => {
      this.applications = apps;
      this.loading = false;
    },
    error: () => {
      this.error = 'Failed to filter candidates';
      this.loading = false;
    }
  });

}
getActiveResume(app: ApplicationModel) {
  const resumes = app.user?.resumes;
  if (!resumes || resumes.length === 0) return null;

  // Supports 'active' (from JSON) and 'isActive' (from Java/Lombok)
  return resumes.find((r: any) => 
    r.active === true || r.isActive === true || r.active === 1
  );
}
getScoreColor(score?: number): string {
  if (score === undefined || score === null) return 'gray';
  if (score >= 75) return 'green';
  if (score >= 50) return 'orange';
  return 'red';
}

updateStatus(appId: number, newStatus: string) {

  this.applicationService.updateStatus(appId, newStatus).subscribe({
    next: updatedApp => {
      // Update UI instantly
      const index = this.applications.findIndex(a => a.id === appId);
      if (index !== -1) {
        this.applications[index].status = updatedApp.status;
      }
    },
    error: err => {
      this.snackBar.open(
        err.error?.message || 'Invalid pipeline transition',
        'Close',
        { duration: 3000 }
      );

    }
  });
}

resetFilters() {
  this.filterForm.reset({
    status: '',
    minScore: '',
    skill: ''
  });
  this.loadCandidates(this.jobId);
}


}







