import { Component, OnInit } from '@angular/core';
import { AnalyticsStats } from '../../../models/analytics.model';
import { Analytics } from '../../../../core/services/analytics';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTableModule } from '@angular/material/table';


@Component({
  selector: 'app-analytics-dashboard',
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatGridListModule,
    MatTableModule
  ],
  templateUrl: './analytics-dashboard.html',
  styleUrl: './analytics-dashboard.scss',
})
export class AnalyticsDashboard implements OnInit{
  
  stats!: AnalyticsStats;
  loading = false;
  error = '';

  constructor(private analyticsService: Analytics) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats() {
    this.loading = true;

    this.analyticsService.getAdminStats().subscribe({
      next: data => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load analytics';
        this.loading = false;
      }
    });
  }
}



