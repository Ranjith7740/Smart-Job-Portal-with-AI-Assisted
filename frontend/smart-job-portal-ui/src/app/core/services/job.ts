import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Job {
  
  private api = `${environment.apiBaseUrl}/jobs`;

  constructor(private http: HttpClient) {}

  createJob(jobData: any): Observable<any> {
    return this.http.post<any>(this.api, jobData);
  }

  getJobs(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.api}?page=${page}&size=${size}`);
  }

  getJobById(id: number) {
    return this.http.get(`${this.api}/${id}`);
  }  
}




