import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import {ResumeModel } from '../../../models/resume.model';
import { Resume } from '../../../../core/services/resume';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button'; 
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { Auth } from '../../../../core/services/auth';

@Component({
  selector: 'app-resume-upload',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatPaginatorModule  
  ],
  templateUrl: './resume-upload.html',
  styleUrl: './resume-upload.scss',
})
export class ResumeUpload implements OnInit {
  
  selectedFile!: File;
  resumes: ResumeModel[] = [];
  loading = false;
  message = '';
  error = '';

  constructor(private resumeService:Resume, private authService: Auth) {}

  ngOnInit(): void {
    this.loadResumes();
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  upload() {
  if (!this.selectedFile) {
    this.error = 'Please select a PDF file';
    return;
  }

  const userId = this.authService.getUserId();
  if (userId === null) {
    this.error = 'User not authenticated';
    return;
  }
  this.loading = true;

  this.resumeService.uploadResume(userId, this.selectedFile).subscribe({
    next: () => {
      this.message = 'Resume uploaded successfully';
      this.error = '';
      this.loading = false;
      this.loadResumes(); // refresh versions
    },
    error: err => {
      this.error = err.error?.message || 'Upload failed';
      this.message = '';
      this.loading = false;
    }
  });
}

loadResumes() {
  const userId = this.authService.getUserId();
  if (userId === null) {
    this.error = 'User not authenticated';
    return;
  }
  this.resumeService.getUserResumes(userId).subscribe({
    next: data => {
      this.resumes = (data as any[]).map(item => ({
        id: item.id,
        filePath: item.filePath,
        active: item.active
      }) as ResumeModel);
    }
  });
}

  
}


