import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import axios from "axios";

@Injectable({
  providedIn: 'root'
})
export class ClientGroupService {
  private _clientGroupsSubject: any;
  private _clientGroups: ClientGroup[] = null;

  constructor() {
    this._clientGroups = [];
    this._clientGroupsSubject = new BehaviorSubject<any>(this._clientGroups);
  }

  get clientGroupsSubject(): Observable<ClientGroup[]> {
    return this._clientGroupsSubject.asObservable();
  }

  setClientGroups(clientGroups: ClientGroup[]) {
    this._clientGroups = clientGroups;
    this._clientGroupsSubject.next(this._clientGroups)
  }

  async getAll(): Promise<ClientGroup[]> {
    let res = await axios.get('oidc-ui/api/v1/piana/oidc/client/all-groups');
    if (res.status === 200) {
      this.setClientGroups(res['data']);
      return res['data'];
    }
    return null;
  }
}

export interface ClientGroup {
  name: string;
}
