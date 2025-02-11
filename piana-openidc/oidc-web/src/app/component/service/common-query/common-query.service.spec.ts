import { TestBed } from '@angular/core/testing';

import { CommonQueryService } from './common-query.service';

describe('CommonQueryService', () => {
  let service: CommonQueryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommonQueryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
