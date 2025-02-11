import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchRoleForUserInDomainDialogComponent } from './search-role-for-user-in-domain-dialog.component';

describe('SearchRoleForUserInDomainDialogComponent', () => {
  let component: SearchRoleForUserInDomainDialogComponent;
  let fixture: ComponentFixture<SearchRoleForUserInDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchRoleForUserInDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchRoleForUserInDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
