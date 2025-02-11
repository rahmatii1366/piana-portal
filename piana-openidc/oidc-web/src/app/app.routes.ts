import { Routes } from '@angular/router';
import { AuthGuard } from './component/auth/auth-gaurd';

export const routes: Routes = [
    {
        path: '', redirectTo: 'root/home', pathMatch: 'full'
    },
    // {
    //     path: 'root',
    //     loadComponent: () => import("./component/root/root.component").then((m) => m.RootComponent),
    // },
    {
        path: 'root',
        loadChildren: () => import("./component/root/root.routes").then((m) => m.routes), canActivate: [AuthGuard],
    },
    {
        path: 'auth/login',
        loadComponent: () => import("./component/auth/login/login.component").then((m) => m.LoginComponent), canActivate: [AuthGuard],
    },
    {
        path: 'auth/change-password',
        loadComponent: () => import("./component/auth/change-password/change-password.component").then((m) => m.ChangePasswordComponent), canActivate: [AuthGuard],
    }
];
