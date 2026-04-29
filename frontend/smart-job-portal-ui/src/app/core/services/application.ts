import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Application {
  private api = `${environment.apiBaseUrl}/applications`;

  constructor(private http: HttpClient) {}

  // Fixes: Property 'apply' does not exist
  apply(jobId: number, userId: number): Observable<any> {
    // Using HttpParams because backend uses @RequestParam
    const params = new HttpParams()
      .set('jobId', jobId.toString())
      .set('userId', userId.toString());
    
    return this.http.post<any>(`${this.api}/apply`, {}, { params });
  }

  // Fixes: Property 'getJobApplications' does not exist
  getJobApplications(jobId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/job/${jobId}`);
  }

  // Other helper methods matching your Controller
  getUserApplications(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/user/${userId}`);
  }

  updateStatus(applicationId: number, status: string): Observable<any> {
    const params = new HttpParams()
      .set('applicationId', applicationId.toString())
      .set('status', status);
    
    return this.http.put<any>(`${this.api}/status`, {}, { params });
  }
  filterCandidates(
    jobId: number,
    status?: string,
    minScore?: number,
    skill?: string
  ): Observable<any[]> {
  let params = new HttpParams().set('jobId', jobId.toString());
  if (status) params = params.set('status', status);
  if (minScore !== undefined) params = params.set('minScore', minScore.toString());
  if (skill) params = params.set('skill', skill);

  return this.http.get<any[]>(`${this.api}/filter`, { params });
}

  

  



}