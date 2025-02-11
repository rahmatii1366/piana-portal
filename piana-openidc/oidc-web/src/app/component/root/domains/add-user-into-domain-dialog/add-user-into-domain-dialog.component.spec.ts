import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddUserIntoDomainDialogComponent } from './add-user-into-domain-dialog.component';

describe('NewDomainDialogComponent', () => {
  let component: AddUserIntoDomainDialogComponent;
  let fixture: ComponentFixture<AddUserIntoDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddUserIntoDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddUserIntoDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
