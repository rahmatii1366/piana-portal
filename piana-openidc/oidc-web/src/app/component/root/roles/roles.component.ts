import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, FormsModule, NgForm, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { RolesService } from './roles.service';
import { MatPaginatorIntlCroService } from '../../shared/mat-paginator-intl-cro/mat-paginator-intl-cro.service';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';
import { MyErrorStateMatcher } from '../domains/domains.component';

@Component({
  selector: 'app-domains',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatPaginatorModule, MatFormFieldModule, MatInputModule, MatTableModule, MatDialogModule, MatExpansionModule, MatIconModule
  ],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.css',
  providers: [
    RolesService,
    { provide: MatPaginatorIntl, useClass: MatPaginatorIntlCroService }
  ]
})
export class RolesComponent {
  readonly dialog = inject(MatDialog);

  insertFormGroup: FormGroup;
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'name', 'description', 'created'];
  size: number = 25;
  offset: number = 0;

  constructor(
    fb: FormBuilder,
    public roleService: RolesService, 
    private showMessageService: ShowMessageService) {
    this.searchFormGroup = fb.group({
      'name': [''],
      // 'date': ['', Validators.required],
      // 'minAmount': [null, Validators.min(0)],
      // 'maxAmount': [null]
    },
      // 👈 after we have finished set the controls we add formGroup validators
      // {  
      //   validators: [maxAmountValidator] 
      // }
    );
    this.insertFormGroup = fb.group({
      'name': ['', Validators.required],
      'description': ['']
    });
  }

  // openNewDialog() {
  //   const dialogRef = this.dialog.open(NewRoleDialogComponent, {
  //     height: '80%',
  //     width: '60%'
  //   });

  //   dialogRef.afterClosed().subscribe(result => {
  //     console.log(`Dialog result: ${result}`);
  //   });
  // }

  loadData() {
    // this.podiumService.load(
    //   this.podiumFormGroup.controls['date'].value,
    //   this.podiumFormGroup.controls['offset'].value,
    //   this.podiumFormGroup.controls['size'].value);
    this.roleService.load(this.offset, this.size);
  }

  insert() {
    if(this.insertFormGroup.valid) {
      console.log('insert');
      this.roleService.create(
        this.insertFormGroup.controls['name'].value,
        this.insertFormGroup.controls['description'].value);
    }
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.roleService.load(this.offset, this.size);
  }
}