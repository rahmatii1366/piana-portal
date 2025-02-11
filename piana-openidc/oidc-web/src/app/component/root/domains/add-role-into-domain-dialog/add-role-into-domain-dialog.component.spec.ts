import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRoleIntoDomainDialogComponent } from './add-role-into-domain-dialog.component';

describe('AddRoleIntoDomainDialogComponent', () => {
  let component: AddRoleIntoDomainDialogComponent;
  let fixture: ComponentFixture<AddRoleIntoDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddRoleIntoDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRoleIntoDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
