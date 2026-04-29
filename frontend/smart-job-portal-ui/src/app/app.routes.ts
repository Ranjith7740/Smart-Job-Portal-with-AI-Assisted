import { Routes } from '@angular/router';
import { Login } from './shared/components/auth/login/login';
import { Register } from './shared/components/auth/register/register';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';





export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    
      { path: 'login', component: Login },
      { path: 'register', component: Register },
      {
        path: 'job-seeker',
        canActivate: [authGuard, roleGuard],
        data: { role: 'JOB_SEEKER' },
        loadChildren: () =>
            import('./shared/components/job-seeker/job-seeker-module').then(m => m.JobSeekerModule)
      },
      // RECRUITER
      {
        path: 'recruiter',
        canActivate: [authGuard, roleGuard],
        data: { role: 'RECRUITER' },
        loadChildren: () =>
            import('./shared/components/recruiter/recruiter-module').then(m => m.RecruiterModule)
    },
    // ADMIN
    {
        path: 'admin',
        canActivate: [authGuard, roleGuard],
        data: { role: 'ADMIN' },
        loadChildren: () =>
            import('./shared/components/admin/admin-module').then(m => m.AdminModule)
    }

];
