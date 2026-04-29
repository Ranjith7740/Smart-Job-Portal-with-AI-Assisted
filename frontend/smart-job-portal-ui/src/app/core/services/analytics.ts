import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { AnalyticsStats } from '../../shared/models/analytics.model';

@Injectable({
  providedIn: 'root',
})
export class Analytics {
  private api = `${environment.apiBaseUrl}/admin/analytics`;

  constructor(private http: HttpClient) {}

  getAdminStats() {
    return this.http.get<AnalyticsStats>(`${this.api}/stats`);
  }

  
}
