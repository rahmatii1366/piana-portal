import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommonQueryService {

  constructor(private http: HttpClient) { }

  async getRolesForDomain(domainId, offset, size) {
    let a$ = this.http.get('oidc-ui/api/v1/piana/oidc/role/for-domain', {
      observe: 'response', params: {
        domainId: domainId, memberOnly: true, offset: offset, size: size
      }
    });

    let res = await lastValueFrom(a$);
    return res;
  }
}

export interface RoleInDomainRequestDto {
  count: number,
  data: RoleInDomainDto[]
}

export interface RoleInDomainDto {
  id: number,
  name: string,
  domainId: number
}
