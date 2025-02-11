import { HttpErrorResponse, HttpEvent, HttpHandler, HttpHandlerFn, HttpInterceptor, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { catchError, Observable, throwError } from "rxjs";
import { inject, Injectable } from "@angular/core";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    console.log("interceptor")
    const token = inject(AuthService).getAuthToken();

    console.log("Token from localStorage:", token);
    if (token) {
        const clonedRequest = req.clone({
            headers: req.headers.set(
                'Authorization', `Bearer ${token}`
            )
        });

        return next(clonedRequest).pipe(
            catchError((err: any) => {
                if (err instanceof HttpErrorResponse) {
                    if (err.status === 401) {
                        console.error('Unauthorized request:', err);
                    } else {
                        console.error('HTTP error:', err);
                    }
                } else {
                    console.error('An error occurred:', err);
                }

                return throwError(() => err);
            })
        );
    } else {
        console.log("no token")
        return next(req);
    }
}