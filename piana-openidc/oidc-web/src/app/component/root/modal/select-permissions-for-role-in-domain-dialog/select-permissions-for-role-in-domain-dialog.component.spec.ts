import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectPermissionsForRoleInDomainDialogComponent } from './select-permissions-for-role-in-domain-dialog.component';

describe('SelectPermissionsForRoleInDomainDialogComponent', () => {
  let component: SelectPermissionsForRoleInDomainDialogComponent;
  let fixture: ComponentFixture<SelectPermissionsForRoleInDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectPermissionsForRoleInDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectPermissionsForRoleInDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
