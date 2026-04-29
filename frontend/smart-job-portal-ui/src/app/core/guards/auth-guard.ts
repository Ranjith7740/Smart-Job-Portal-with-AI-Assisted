import { CanActivateFn } from '@angular/router';
import { Auth } from '../services/auth';
import { inject } from '@angular/core';
import { Router } from '@angular/router';


export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth); // Inject service without constructor
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Not logged in → redirect to login
  router.navigate(['/login']);
  return false;
};
