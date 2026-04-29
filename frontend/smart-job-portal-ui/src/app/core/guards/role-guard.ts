import { CanActivateFn } from '@angular/router';
import { Auth } from '../services/auth';
import { inject } from '@angular/core';
import { Router } from '@angular/router';



export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);

  // Get the role required for this specific route
  const expectedRole = route.data['role'];
  const userRole = authService.getRole();

  // 1. Not logged in at all?
  if (!userRole) {
    router.navigate(['/login']);
    return false;
  }

  // 2. Does the role match?
  if (userRole === expectedRole) {
    return true;
  }

  // 3. Logged in but unauthorized (Wrong role)
  // You might want to redirect to a 'forbidden' page or dashboard instead
  router.navigate(['/unauthorized']); 
  return false;
};
