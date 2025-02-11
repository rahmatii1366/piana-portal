import { TestBed } from '@angular/core/testing';

import { SearchRoleForUserInDomainService } from './search-role-for-user-in-domain.service';

describe('SearchRoleForUserInDomainService', () => {
  let service: SearchRoleForUserInDomainService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchRoleForUserInDomainService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
