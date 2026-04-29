export interface TokenPayload {
    sub: string;     // subject (user identifier)
  role: string;
  userId: number;    // optional (future-proof)
  exp: number;
}

