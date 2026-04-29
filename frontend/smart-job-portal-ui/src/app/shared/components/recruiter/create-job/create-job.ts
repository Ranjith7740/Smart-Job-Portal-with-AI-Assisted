import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Job } from '../../../../core/services/job';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatProgressBarModule } from '@angular/material/progress-bar';


@Component({
  selector: 'app-create-job',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule
  ],
  templateUrl: './create-job.html',
  styleUrl: './create-job.scss',
})
export class CreateJob {
  
  loading = false;
  message = '';
  error = '';
  form: any;

  

  constructor(
    private fb: FormBuilder,
    private jobService: Job,
    private router: Router
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      requiredSkills: ['', Validators.required]
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.loading = true;
    this.message = '';
    this.error = '';

    this.jobService.createJob(this.form.value as any).subscribe({
      next: () => {
        this.message = 'Job created successfully';
        this.loading = false;
        this.form.reset();
      },
      error: err => {
        this.error = err.error?.message || 'Failed to create job';
        this.loading = false;
      }
    });
  }
}



