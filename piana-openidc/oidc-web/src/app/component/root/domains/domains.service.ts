import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';

@Injectable({
  providedIn: 'root'
})
export class DomainsService {
  private _statement: DomainDtoResponse;
  private _statementSubject: BehaviorSubject<DomainDtoResponse>;

  constructor(private http: HttpClient, private showMessage: ShowMessageService) {
    this._statement = { count: 0, data: [] };
    this._statementSubject = new BehaviorSubject<DomainDtoResponse>(this._statement);
  }

  get statement(): Observable<DomainDtoResponse> {
    return this._statementSubject.asObservable();
  }

  set statement(_statement: any) {
    this._statement = _statement;
    this._statementSubject.next(this._statement);
  }

  load(offset: number, size: number) {
    this.http.get('oidc-ui/api/v1/piana/oidc/domain/all', { observe: 'response', params: { offset: offset, size: size } })
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

  create(domainName: string, domainDescription: string) {
    this.http.post('oidc-ui/api/v1/piana/oidc/domain/create',
      { name: domainName, descriptoin: domainDescription },
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

  addUsersByRoles(domainId: number, userIds: number[], roleIds: number[]) {
    return this.http.post('oidc-ui/api/v1/piana/oidc/domain/add-users-by-roles',
      { domainId: domainId, userIds: userIds, roleIds: roleIds },
      { observe: 'response' });
  }

  addRoleByPermissions(domainId: number, roleId: number, permissionIds: number[]) {
    return this.http.post('oidc-ui/api/v1/piana/oidc/domain/add-role-by-permissions',
      { domainId: domainId, roleId: roleId, permissionIds: permissionIds },
      { observe: 'response' });
  }

}

export interface DomainDtoResponse {
  count: number,
  data: DomainDto[]
}

export interface DomainDto {
  id: number,
  name: string,
  description: string,
  persianDateTime: string,
}
