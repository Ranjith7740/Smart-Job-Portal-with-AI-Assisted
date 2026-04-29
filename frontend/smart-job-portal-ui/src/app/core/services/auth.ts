import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse } from '../../shared/models/auth.model';
import { TokenPayload } from '../../shared/models/token-payload.model';
import { tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode'; // Fix: Ensure this is installed via npm

@Injectable({
  providedIn: 'root',
})
export class Auth { // Renamed to AuthService for clarity
  private readonly api = `${environment.apiBaseUrl}/auth`;

  constructor(private http: HttpClient) {}

  register(data: AuthRequest) {
    return this.http.post<AuthResponse>(`${this.api}/register`, data);
  }

  login(data: AuthRequest) {
    return this.http.post<AuthResponse>(`${this.api}/login`, data).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.clear();
  }

  getDecodedToken(): TokenPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode<TokenPayload>(token);
    } catch {
      return null;
    }
  }

  getUserEmail(): string | null {
    return this.getDecodedToken()?.sub ?? null;
  }

  getUserRole(): string | null {
    return this.getDecodedToken()?.role ?? null;
  }

  getUserId(): number | null {
    // This is vital for fixing your "No value present" backend error!
    return this.getDecodedToken()?.userId ?? null;
  }
}