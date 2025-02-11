import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectRoleForDomainDialogComponent } from './select-role-for-domain-dialog.component';

describe('SelectRoleForDomainDialogComponent', () => {
  let component: SelectRoleForDomainDialogComponent;
  let fixture: ComponentFixture<SelectRoleForDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectRoleForDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectRoleForDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
