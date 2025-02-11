import { Routes } from '@angular/router';
import { AuthGuard } from '../auth/auth-gaurd';

export const routes: Routes = [
    {
        path: '', loadComponent: () => import("./root.component").then((m) => m.RootComponent),
        children: [
            { path: 'home', loadComponent: () => import("./home/home.component").then((m) => m.HomeComponent) },
            { path: 'domains', loadComponent: () => import("./domains/domains.component").then((m) => m.DomainsComponent), canActivate: [AuthGuard] },
            { path: 'roles', loadComponent: () => import("./roles/roles.component").then((m) => m.RolesComponent), canActivate: [AuthGuard] },
            { path: 'permissions', loadComponent: () => import("./permissions/permissions.component").then((m) => m.PermissionsComponent), canActivate: [AuthGuard] },
            { path: 'users', loadComponent: () => import("./users/users.component").then((m) => m.UsersComponent), canActivate: [AuthGuard] }
        ]
    }
];
