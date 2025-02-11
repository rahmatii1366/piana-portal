import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router"
import { lastValueFrom, Observable } from "rxjs"
import { AuthService } from "./auth.service"
import { Injectable } from "@angular/core"
import { MessageType, ShowMessageService } from "../service/show-message/show-message.service"

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private authService: AuthService, 
        private showMessage: ShowMessageService,
        private router: Router) { }

    async canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot):
        Promise<boolean | UrlTree>
     {
        console.log('canActivate', state.url, route.url)

        if (state.url === '/auth/change-password') {
            if (!this.authService.isLoggedIn()) {
                return this.router.createUrlTree(['/auth/login']);
            } else if (this.authService.shouldBeChangePassword()) {
                return true;
            } else {
                return this.router.createUrlTree(['/root/home'])     
            }
        } else if (state.url === '/auth/login') {
            if (this.authService.isLoggedIn()) {
                if (this.authService.shouldBeChangePassword()) {
                    return this.router.createUrlTree(['/auth/change-password']);
                } else {
                    return this.router.createUrlTree(['/root/home']);
                }
            }
            else {
                return true;
            }
        } else {
            console.log("else", this.authService.isLoggedIn(), state.url)
            if (this.authService.isLoggedIn()) {
                switch (state.url) {
                    case '/root/domains': {
                        console.log(1)
                        let a$ = this.authService.hasPermission('PERM_DOMAINS_PAGE');
                        let res = await lastValueFrom(a$);
                        console.log('i', res)
                        if (res.status == 200 && res.body['data'])
                            return true;
                        else {
                            this.showMessage.addMessage({
                                messageType: MessageType.INFO,
                                description: "dont have permission",
                                title: "permission failed"
                            });
                            return false;
                        }
                    }
                    // case '/root/roles': {
                    //     console.log(2)
                    //     let res = this.authService.hasPermissionFormUI('PERM_ROLES_PAGE');
                    //     // console.log('/root/roles', res)
                    //     if (res) 
                    //         return true;
                    //     else {
                    //         this.showMessage.addMessage({
                    //             messageType: MessageType.INFO,
                    //             description: "dont have permission",
                    //             title: "permission failed"
                    //         });
                    //         return false;
                    //     }
                    // }
                    case '/root/permissions': {
                        console.log(3)
                        let a$ = this.authService.hasPermission('PERM_PERMISSIONS_PAGE');
                        let res = await lastValueFrom(a$);
                        console.log('i', res)
                        if (res.status == 200 && res.body['data'])
                            return true;
                        else {
                            this.showMessage.addMessage({
                                messageType: MessageType.INFO,
                                description: "dont have permission",
                                title: "permission failed"
                            });
                            return false;
                        }
                    }
                    case '/root/users': {
                        console.log(4)
                        let a$ = this.authService.hasPermission('PERM_USERS_PAGE');
                        let res = await lastValueFrom(a$);
                        console.log('i', res)
                        if (res.status == 200 && res.body['data'])
                            return true;
                        else {
                            this.showMessage.addMessage({
                                messageType: MessageType.INFO,
                                description: "dont have permission",
                                title: "permission failed"
                            });
                            return false;
                        }
                    }
                }
                console.log(5)
                return true;
            } else {
                return this.router.createUrlTree(['/auth/login']);
            }
        }

        // if (this.authService.isLoggedIn())
        return true
        // return this.router.createUrlTree(['/auth/login'])
    }

}