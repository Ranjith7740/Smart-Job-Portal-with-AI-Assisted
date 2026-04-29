export interface AuthRequest {
  email: string;
  password: string;
  name?: string; // only for register
}

export interface AuthResponse {
  token: string;
  role: 'JOB_SEEKER' | 'RECRUITER' | 'ADMIN';
}