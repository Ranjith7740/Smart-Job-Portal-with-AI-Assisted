import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Resume {
  
  private api = `${environment.apiBaseUrl}/resumes`;

  constructor(private http: HttpClient) {}

  uploadResume(userId: number, file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<Resume>(
      `${this.api}/upload?userId=${userId}`,
      formData
    );
  }

  getUserResumes(userId: number) {
    return this.http.get<Resume[]>(`${this.api}/user/${userId}`);
  }

  getActiveResume(userId: number) {
    return this.http.get<Resume>(`${this.api}/active/${userId}`);
  }  
}



