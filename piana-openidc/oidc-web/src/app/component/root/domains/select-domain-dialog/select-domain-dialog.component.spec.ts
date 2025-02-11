import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectDomainDialogComponent } from './select-domain-dialog.component';

describe('SelectDomainDialogComponent', () => {
  let component: SelectDomainDialogComponent;
  let fixture: ComponentFixture<SelectDomainDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectDomainDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectDomainDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
