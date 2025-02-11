import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ShowMessageService } from '../../../service/show-message/show-message.service';

@Injectable()
export class SearchRoleForUserInDomainService {
  private _role: RolePerUserInDomainDtoResponse;
  private _roleSubject: BehaviorSubject<RolePerUserInDomainDtoResponse>;

  constructor(private http: HttpClient, private showMessage: ShowMessageService) {
    this._role = { count: 0, data: [] };
    this._roleSubject = new BehaviorSubject<RolePerUserInDomainDtoResponse>(this._role);
  }

  get roles(): Observable<RolePerUserInDomainDtoResponse> {
    return this._roleSubject.asObservable();
  }

  set roles(_role: any) {
    this._role = _role;
    this._roleSubject.next(this._role);
  }

  load(offset: number, size: number,
    name: string) {
    this.http.get('oidc-ui/api/v1/piana/oidc/role/per-user-on-domain', {
      observe: 'response', params: {
        offset: offset, size: size, name: name
      }
    })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.roles = res.body;
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }

  // create(firstName: string, lastName: string, username: string) {
  //   this.http.post('oidc-ui/api/v1/piana/oidc/user/create',
  //     { firstName: firstName, lastName: lastName, username: username },
  //     { observe: 'response' })
  //     .subscribe({
  //       next: res => {
  //         if (res.status == 200) {
  //           this.showMessage.addMessage({
  //             messageType: MessageType.SUCCESS,
  //             title: "success",
  //             description: "insert successfully!"
  //           });
  //         } else {
  //           console.log('error');
  //         }
  //       }, error: err => {
  //         console.log('error =>', err);
  //       }, complete: () => {
  //       }
  //     });
  // }
}


export interface RolePerUserInDomainDtoResponse {
  count: number,
  data: RolePerUserInDomainDto[]
}

export interface RolePerUserInDomainDto {
  id: number,
  name: string,
  domainId: number
}
