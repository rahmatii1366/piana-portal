import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ShowMessageService } from '../../service/show-message/show-message.service';

@Injectable()
export class UserDomainService {
private _dto: UserDomainsByItsDtoResponse;
  private _dtoSubject: BehaviorSubject<UserDomainsByItsDtoResponse>;

  constructor(private http: HttpClient, private showMessage: ShowMessageService) {
    this._dto = {userId: 0, domains: []};
    this._dtoSubject = new BehaviorSubject<UserDomainsByItsDtoResponse>(this._dto);
  }

  get dto(): Observable<UserDomainsByItsDtoResponse> {
    return this._dtoSubject.asObservable();
  }

  set dto(_dto: any) {
    console.log(_dto)
    this._dto = _dto['data'];
    this._dtoSubject.next(this._dto);
  }

  reload(userId: number) {
    this.http.get('oidc-ui/api/v1/piana/oidc/user/domains-and-its-roles', { observe: 'response', params: { userId: userId } })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.dto = res.body;
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }

  public getRoles(domain: SimpleDomainByRolesDto): string {
    let s = Array.from(domain.roles).map(role => role.name);
    return s.flat().join(",");
  }
}

export interface UserDomainsByItsDtoResponse {
  userId: number,
  domains: SimpleDomainByRolesDto[]
}

export interface SimpleDomainByRolesDto {
  domainId: number,
  domainName: string,
  roles: SimpleRoleDto[]
}

export interface SimpleRoleDto {
  id: number,
  name: string
}


