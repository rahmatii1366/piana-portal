import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private _statement: UserDtoResponse;
  private _statementSubject: BehaviorSubject<UserDtoResponse>;

  constructor(private http: HttpClient, private showMessage: ShowMessageService) {
    this._statement = { count: 0, data: [] };
    this._statementSubject = new BehaviorSubject<UserDtoResponse>(this._statement);
  }

  get statement(): Observable<UserDtoResponse> {
    return this._statementSubject.asObservable();
  }

  set statement(_statement: any) {
    this._statement = _statement;
    this._statementSubject.next(this._statement);
  }

  load(offset: number, size: number) {
    this.http.get('oidc-ui/api/v1/piana/oidc/user/all', { observe: 'response', params: { offset: offset, size: size } })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.statement = res.body;
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }

  create(firstName: string, lastName: string, username: string, password: string) {
    this.http.post('oidc-ui/api/v1/piana/oidc/user/create',
      { firstName: firstName, lastName: lastName, username: username, password: password },
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

export interface UserDtoResponse {
  count: number,
  data: UserDto[]
}

export interface UserDto {
  id: number,
  firstName: string,
  lastName: string,
  username: string
}
