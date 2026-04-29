import { Component } from '@angular/core';
import { JobModel } from '../../../models/job.model';
import { ActivatedRoute } from '@angular/router';
import { Job } from '../../../../core/services/job';
import { Auth } from '../../../../core/services/auth';
import { OnInit } from '@angular/core';
import { Application } from '../../../../core/services/application';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-job-details',
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatPaginatorModule
  ],
  templateUrl: './job-details.html',
  styleUrl: './job-details.scss',
})
export class JobDetails implements OnInit {

  job!: JobModel;
  loading = false;
  message = '';
  error = '';

  constructor(
    private route: ActivatedRoute,
    private jobService: Job,
    private applicationService: Application,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    const jobId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadJob(jobId);
  }

  loadJob(jobId: number) {
    this.loading = true;

    this.jobService.getJobById(jobId).subscribe({
      next: job => {
        this.job = job as JobModel;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load job';
        this.loading = false;
      }
    });
  }

 apply() {
    const userId = this.authService.getUserId();
    
    // Debugging: Check the console to see if this is null
    console.log("Attempting to apply. Job ID:", this.job.id, "User ID:", userId);

    if (!userId) {
      this.error = 'User ID not found. Please log out and log back in.';
      return;
    }

    this.applicationService.apply(this.job.id, userId).subscribe({
      next: () => {
        this.message = 'Successfully applied to the job!';
        this.error = '';
      },
      error: (err: any) => {
        // This will catch the "Duplicate application" error from your backend
        this.error = err.error?.message || 'You have already applied for this position.';
        this.message = '';
      }
    });
}
  

  private getLoggedInUserId(): number {
    // TEMP approach (matches current backend design)
    // Later this should be decoded from JWT
    return Number(localStorage.getItem('userId'));
  }
}





