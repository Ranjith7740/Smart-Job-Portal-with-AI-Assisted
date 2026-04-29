import { Component, OnInit } from '@angular/core';
import { JobModel } from '../../../models/job.model';
import { Job } from '../../../../core/services/job';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatPaginatorModule,
    RouterModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard  implements OnInit{
    jobs: JobModel[] = [];
  loading = false;
  error = '';

  constructor(private jobService: Job) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs() {
    this.loading = true;

    // Using existing API: GET /api/jobs
    this.jobService.getJobs(0, 20).subscribe({
      next: res => {
        this.jobs = res.content;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load jobs';
        this.loading = false;
      }
    });
  }
}




