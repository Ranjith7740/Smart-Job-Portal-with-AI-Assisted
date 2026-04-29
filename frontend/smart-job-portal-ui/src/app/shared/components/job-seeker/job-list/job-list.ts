import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import {JobModel } from '../../../models/job.model';

import { CommonModule } from '@angular/common';
import { Job } from '../../../../core/services/job';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button'; 
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { RouterModule } from '@angular/router';



@Component({
  selector: 'app-job-list',
  imports: [CommonModule,RouterModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatPaginatorModule
  ],
  templateUrl: './job-list.html',
  styleUrl: './job-list.scss',
})
export class JobList implements OnInit {
  jobs:JobModel[] = [];
  page = 0;
  size = 5;
  totalPages = 0;
  loading = false;

  constructor(private jobService: Job) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.loading = true;

    this.jobService.getJobs(this.page, this.size).subscribe({
      next: res => {
        this.jobs = res.content;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadJobs();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadJobs();
    }
  }
}


