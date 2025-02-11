import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogContent, MatDialogModule, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RolePerUserInDomainDto, SearchRoleForUserInDomainService } from './search-role-for-user-in-domain.service';
import { MyErrorStateMatcher } from '../../domains/domains.component';

@Component({
  selector: 'app-search-role-for-user-in-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatPaginatorModule, MatTableModule,
    MatDialogTitle, MatDialogContent, MatTooltipModule, MatCheckboxModule
  ],
  templateUrl: './search-role-for-user-in-domain-dialog.component.html',
  styleUrl: './search-role-for-user-in-domain-dialog.component.css',
  providers: [
    SearchRoleForUserInDomainService
  ]
})
export class SearchRoleForUserInDomainDialogComponent {
  readonly dialogRef = inject(MatDialogRef<SearchRoleForUserInDomainDialogComponent>);
  data = inject(MAT_DIALOG_DATA);
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'name', 'description'];
  displayedColumnsWithCommand: string[] = [...this.displayedColumns, 'operation'];
  size: number = 25;
  offset: number = 0;
  public dialogData: any;

  public selectedRoles: RolePerUserInDomainDto[] = [];

  constructor(fb: FormBuilder, public searchRoleService: SearchRoleForUserInDomainService) {
    console.log('data', this.data)
    this.searchFormGroup = fb.group({
      'name': ['']
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.searchFormGroup.controls; }

  addRoles(e, element: RolePerUserInDomainDto) {
    if (e['checked'])
      this.selectedRoles.push(element);
    else {
      const index = this.selectedRoles.indexOf(element, 0);
      if (index > -1) {
        this.selectedRoles.splice(index, 1);
      }
    }
    console.log(this.selectedRoles);
  }

  ok() {
    this.dialogRef.close(this.selectedRoles);
  }

  loadData() {
    // this.podiumService.load(
    //   this.podiumFormGroup.controls['date'].value,
    //   this.podiumFormGroup.controls['offset'].value,
    //   this.podiumFormGroup.controls['size'].value);
    this.searchRoleService.load(this.offset, this.size,
      this.f['name'].value);
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.searchRoleService.load(this.offset, this.size,
      this.f['name'].value);
  }
}