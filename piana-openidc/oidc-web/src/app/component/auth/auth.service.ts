import { Injectable } from '@angular/core';
import { BehaviorSubject, lastValueFrom, Observable } from 'rxjs';
import { MessageType, ShowMessageService } from '../service/show-message/show-message.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LocalStorageService } from '../service/local-storage/local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _auth: Authentication;
  private _authSubject: BehaviorSubject<Authentication>;

  private _domains: Domain[];
  private _domainsSubject: BehaviorSubject<Domain[]>;

  private _uiPermissions: Permission[];
  private _uiPermissionsSubject: BehaviorSubject<Permission[]>;

  constructor(
    private localStorage: LocalStorageService,
    private http: HttpClient,
    private showMessage: ShowMessageService,
    private router: Router) {
    this._domains = [];
    this._domainsSubject = new BehaviorSubject(this._domains);

    this._uiPermissions = [];
    this._uiPermissionsSubject = new BehaviorSubject(this._uiPermissions);


    this._auth = {
      jwtToken: null,
      isLoggedIn: false,
      shouldBeChangePassword: false,
      username: null,
      uiPermissions: []
    };

    this._authSubject = new BehaviorSubject(this._auth);
  }

  getAuthToken() {
    let jwtToken = this.localStorage.getItem('jwtToken');
    return jwtToken;
  }

  isLoggedIn(): boolean {
    console.log('isLoggedIn', this._auth.isLoggedIn)
    return this._auth && this._auth.isLoggedIn
  }

  shouldBeChangePassword(): boolean {
    console.log('shouldBeChangePassword', this._auth.shouldBeChangePassword)
    return this._auth && this._auth.shouldBeChangePassword
  }

  get domains(): Observable<Domain[]> {
    return this._domainsSubject.asObservable();
  }

  set domains(_domains: Domain[]) {
    console.log(_domains);
    this._domains = _domains;
    this._domainsSubject.next(this._domains);
  }

  get uiPermissions(): Observable<Permission[]> {
    return this._uiPermissionsSubject.asObservable();
  }

  getUIPermissionId(uiPermissionName: string): number {
    // console.log(uiPermissionName);
    if (this._uiPermissions == null || this._uiPermissions.length == 0)
      return 0;
    let perm = this._uiPermissions.filter((value, index, array) => {
      if (value.name === uiPermissionName)
        return true;
      return false;
    });
    // console.log(perm, perm == null ? 0 : perm[0].id);
    return perm == null ? 0 : perm[0].id;
  }


  set uiPermissions(_uiPermissions: Permission[]) {
    console.log(_uiPermissions);
    this._uiPermissions = _uiPermissions;
    this._uiPermissionsSubject.next(this._uiPermissions);
  }

  get auth(): Observable<Authentication> {
    return this._authSubject.asObservable();
  }

  set auth(_auth: Authentication) {
    console.log(_auth)
    this._auth = _auth;
    this._authSubject.next(this._auth);
  }

  hasPermission(perm: string) {
    return this.http.get('oidc-ui/api/v1/piana/oidc/auth/has-permission?perm=' + perm,
      { observe: 'response' });
  }

  hasPermission2(perm: string) {
    return this.http.get('oidc-ui/api/v1/piana/oidc/auth/has-permission?perm=' + perm,
      {}).toPromise();
  }

  hasPermissionFormUI(perm: string): boolean {
    let i = this.getUIPermissionId(perm);
    if (i <= 0)
      return false;
    // console.log(this._auth.uiPermissions)
    if (!this._auth.uiPermissions)
      return false;
    return this._auth.uiPermissions.includes(i);

    // let a$ = this.http.get('oidc-ui/api/v1/auth/has-permission?perm=' + perm,
    //   {});
    // let res = await lastValueFrom(a$);
    // console.log("res = " + res['data']);
    // return res['data'];
  }

  hasAnyPermissionFormUI(...perms: string[]): boolean {
    let l = perms.filter(perm => {
      let i = this.getUIPermissionId(perm);
      if (i <= 0)
        return false;
      // console.log(this._auth.uiPermissions)
      if (!this._auth.uiPermissions)
        return false;
      return this._auth.uiPermissions.includes(i);
    });

    console.log('hasAnyPermissionFormUI', l, this._auth.uiPermissions);
    return l.length > 0;

    // let a$ = this.http.get('oidc-ui/api/v1/auth/has-permission?perm=' + perm,
    //   {});
    // let res = await lastValueFrom(a$);
    // console.log("res = " + res['data']);
    // return res['data'];
  }

  async refreshAuthState() {
    console.log("refreshAuthState");
    this.refreshDomains();
    this.refreshUIPermission();
    let jwtToken = this.localStorage.getItem('jwtToken');
    if (jwtToken === null) {
      console.log('jwtToken not exist!', jwtToken);
      return null;
    } else {
      console.log('jwtToken exist!', jwtToken, jwtToken === null, jwtToken === 'null');

      let res = null;
      try {
        let a$ = this.http.get('oidc-ui/api/v1/piana/oidc/auth/refresh',
          { observe: 'response', headers: { 'Authorization': 'Bearer ' + jwtToken } }
        );

        res = await lastValueFrom(a$);

        if (res.status == 200) {
          this.showMessage.addMessage({
            messageType: MessageType.SUCCESS,
            title: "success",
            description: "refresh info successfully"
          });
          this.auth = {
            jwtToken: jwtToken,
            isLoggedIn: true,
            shouldBeChangePassword: res.body['data']['shouldBeChangePassword'],
            username: res.body['data']['username'],
            uiPermissions: res.body['data']['uiPermissions']
          };
          // this.localStorage.setItem('jwtToken', res.body['data']['jwtToken']);
          if (this._auth.shouldBeChangePassword) {
            console.log('navigate to change password')
            this.router.navigateByUrl("auth/change-password")
          } else {
            // this.router.navigateByUrl("root/home")
          }
        } else {
          console.log('error');
        }

      } catch (e) {
        console.log("catch error", e);
        return null;
      } finally {
        console.log('is promise', res);
      }

      // ).subscribe({
      //   next: (res) => {
      //     console.log(res)
      //     if (res.status == 200) {
      //       this.showMessage.addMessage({
      //         messageType: MessageType.SUCCESS,
      //         title: "success",
      //         description: "refresh info successfully"
      //       });
      //       this.auth = {
      //         jwtToken: jwtToken,
      //         isLoggedIn: true,
      //         shouldBeChangePassword: res.body['data']['shouldBeChangePassword'],
      //         username: res.body['username']
      //       };
      //       // this.localStorage.setItem('jwtToken', res.body['data']['jwtToken']);
      //       if (this._auth.shouldBeChangePassword) {
      //         console.log('navigate to change password')
      //         this.router.navigateByUrl("auth/change-password")
      //       } else {
      //         // this.router.navigateByUrl("root/home")
      //       }
      //     } else {
      //       console.log('error');
      //     }
      //   }, error: (err) => {

      //   }, complete: () => {

      //   }
      // });

      // return await this.http.get('oidc-ui/api/v1/auth/refresh',
      //   { observe: 'response', headers: { 'Authorization': 'Bearer ' + jwtToken } }
      // ).subscribe({
      //   next: (res) => {
      //     console.log(res)
      //     if (res.status == 200) {
      //       this.showMessage.addMessage({
      //         messageType: MessageType.SUCCESS,
      //         title: "success",
      //         description: "refresh info successfully"
      //       });
      //       this.auth = {
      //         jwtToken: jwtToken,
      //         isLoggedIn: true,
      //         shouldBeChangePassword: res.body['data']['shouldBeChangePassword'],
      //         username: res.body['username']
      //       };
      //       // this.localStorage.setItem('jwtToken', res.body['data']['jwtToken']);
      //       if (this._auth.shouldBeChangePassword) {
      //         console.log('navigate to change password')
      //         this.router.navigateByUrl("auth/change-password")
      //       } else {
      //         // this.router.navigateByUrl("root/home")
      //       }
      //     } else {
      //       console.log('error');
      //     }
      //   }, error: (err) => {

      //   }, complete: () => {

      //   }
      // });
    }
  }

  async refreshDomains() {
    console.log("refreshDomains");
    let res = null;

    let a$ = this.http.get('/oidc-ui/api/v1/piana/oidc/domain/all',
      { observe: 'response', headers: {} }
    );

    res = await lastValueFrom(a$);

    if (res.status == 200) {
      console.log(res.body)
      this.domains = res.body['data']
    }
  }

  async refreshUIPermission() {
    console.log("refreshUIPermissions");
    let res = null;

    let a$ = this.http.get('/oidc-ui/api/v1/piana/oidc/permission/all-ui-permissions',
      { observe: 'response', headers: {} }
    );

    res = await lastValueFrom(a$);

    if (res.status == 200) {
      console.log(res.body)
      this.uiPermissions = res.body['data'];
    }
  }

  changePassword(password: string, newPassword: string) {
    this.http.post('oidc-ui/api/v1/piana/oidc/auth/change-password',
      { password: password, newPassword: newPassword },
      { observe: 'response', headers: { 'Authorization': 'Bearer ' + this._auth.jwtToken } }
    ).subscribe({
      next: (res) => {
        if (res.status == 200) {
          this.showMessage.addMessage({
            messageType: MessageType.SUCCESS,
            title: "success",
            description: "change password successfully!"
          });
          this.auth = {
            jwtToken: this._auth.jwtToken,
            isLoggedIn: this._auth.isLoggedIn,
            shouldBeChangePassword: false,
            username: this._auth.username,
            uiPermissions: this._auth.uiPermissions
          };
          if (this._auth.shouldBeChangePassword) {
            console.log('navigate to change password')
            this.router.navigateByUrl("auth/change-password")
          } else {
            this.router.navigateByUrl("root/domains")
          }
        } else {
          console.log('error');
        }
      }, error: (err) => {

      }, complete: () => {

      }
    });
  }

  login(username: string, password: string, domainId: number) {
    this.http.post('/oidc-ui/api/v1/piana/oidc/auth/login',
      { username: username, password: password, domainId: domainId, channel: 'WEB' },
      { observe: 'response' }
    ).subscribe({
      next: (res) => {
        if (res.status == 200) {
          this.showMessage.addMessage({
            messageType: MessageType.SUCCESS,
            title: "success",
            description: "insert successfully!"
          });
          this.auth = {
            jwtToken: res.body['data']['jwtToken'],
            isLoggedIn: true,
            shouldBeChangePassword: res.body['data']['shouldBeChangePassword'],
            username: res.body['data']['username'],
            uiPermissions: res.body['data']['uiPermissions']
          };
          this.localStorage.setItem('jwtToken', res.body['data']['jwtToken']);
          if (this._auth.shouldBeChangePassword) {
            console.log('navigate to change password')
            this.router.navigateByUrl("auth/change-password")
          } else {
            console.log('navigate to root')
            this.router.navigateByUrl("root/home")
          }
        } else {
          console.log('error');
        }
      }, error: (err) => {

      }, complete: () => {

      }
    });
  }

  public logout() {
    this.http.get('oidc-ui/api/v1/piana/oidc/auth/logout',
      { observe: 'response' }
    ).subscribe({
      next: (res) => {
        if (res.status == 200) {
          this.showMessage.addMessage({
            messageType: MessageType.SUCCESS,
            title: "success",
            description: "insert successfully!"
          });
          this.auth = {
            jwtToken: null,
            isLoggedIn: false,
            shouldBeChangePassword: false,
            username: '',
            uiPermissions: []
          };
          this.localStorage.removeItem('jwtToken');
          this.router.navigateByUrl("auth/login")
        } else {
          console.log('error');
        }
      }, error: (err) => {

      }, complete: () => {

      }
    });
  }
}

export interface Authentication {
  jwtToken: string,
  isLoggedIn: boolean,
  shouldBeChangePassword: boolean,
  username: string,
  uiPermissions: number[]
}

export interface Domain {
  id: number,
  name: string
}

export interface PermissionDtoResponse {
  count: number,
  data: Permission[]
}

export interface Permission {
  id: number,
  name: string
}
