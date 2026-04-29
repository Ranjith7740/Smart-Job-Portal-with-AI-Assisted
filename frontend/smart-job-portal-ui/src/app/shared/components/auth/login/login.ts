import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../../../core/services/auth';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatError } from '@angular/material/form-field';


@Component({
  selector: 'app-login',
  imports: [CommonModule,ReactiveFormsModule,RouterModule,MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatError],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loading = false;
  errorMessage = '';
  form: any;


  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  login() {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.form.value as any).subscribe({
      next: res => {
        // Role-based routing (CRITICAL)
        switch (res.role) {
          case 'JOB_SEEKER':
            this.router.navigate(['/job-seeker']);
            break;
          case 'RECRUITER':
            this.router.navigate(['/recruiter']);
            break;
          case 'ADMIN':
            this.router.navigate(['/admin']);
            break;
        }
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Login failed';
        this.loading = false;
      }
    });
  }
}


