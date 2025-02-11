import { TestBed } from '@angular/core/testing';

import { MatPaginatorIntlCroService } from './mat-paginator-intl-cro.service';

describe('MatPaginatorIntlCroService', () => {
  let service: MatPaginatorIntlCroService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MatPaginatorIntlCroService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
