import { Injectable } from '@angular/core';
import { BehaviorSubject, first, Observable } from 'rxjs';
import { MessageType, ShowMessageService } from '../../../service/show-message/show-message.service';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class SearchUserService {
private _user: UserPerDomainDtoResponse;
  private _userSubject: BehaviorSubject<UserPerDomainDtoResponse>;

  constructor(private http: HttpClient, private showMessage: ShowMessageService) {
    this._user = { count: 0, data: [] };
    this._userSubject = new BehaviorSubject<UserPerDomainDtoResponse>(this._user);
  }

  get users(): Observable<UserPerDomainDtoResponse> {
    return this._userSubject.asObservable();
  }

  set users(_user: any) {
    this._user = _user;
    this._userSubject.next(this._user);
  }

  load(offset: number, size: number,
    firstName: string, lastName: string, username: string, noMemberOnly: boolean,
     domainId: number) {
    this.http.get('oidc-ui/api/v1/piana/oidc/user/per-domain', { observe: 'response', params: {
       offset: offset, size: size, firstName: firstName, lastName: lastName, username: username, noMemberOnly: noMemberOnly, domainId: domainId
       } })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.users = res.body;
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }

  create(firstName: string, lastName: string, username: string) {
    this.http.post('oidc-ui/api/v1/piana/oidc/user/create',
      { firstName: firstName, lastName: lastName, username: username },
      { observe: 'response' })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.showMessage.addMessage({
              messageType: MessageType.SUCCESS,
              title: "success",
              description: "insert successfully!"
            });
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }
}

export interface UserPerDomainDtoResponse {
  count: number,
  data: UserPerDomainDto[]
}

export interface UserPerDomainDto {
  id: number,
  firstName: string,
  lastName: string,
  username: string,
  domainId: number
}
