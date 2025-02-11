import { CommonModule } from '@angular/common';
import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogContent, MatDialogModule, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MyErrorStateMatcher } from '../../domains/domains.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { SearchUserService, UserPerDomainDto } from './search-user.service';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCheckboxModule } from '@angular/material/checkbox';
@Component({
  selector: 'app-search-user',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatPaginatorModule, MatTableModule,
    MatDialogTitle, MatDialogContent, MatTooltipModule, MatCheckboxModule
  ],
  templateUrl: './search-user-dialog.component.html',
  styleUrl: './search-user-dialog.component.css',
  providers: [
    SearchUserService
  ]
})
export class SearchUserDialogComponent {
  readonly dialogRef = inject(MatDialogRef<SearchUserDialogComponent>);
  data = inject(MAT_DIALOG_DATA);
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'username'];
  displayedColumnsWithCommand: string[] = [...this.displayedColumns, 'state', 'operation'];
  size: number = 25;
  offset: number = 0;
  public dialogData: any;

  public selectedUsers: UserPerDomainDto[] = [];

  constructor(fb: FormBuilder, public searchUserService: SearchUserService) {
    console.log('data', this.data)
    this.searchFormGroup = fb.group({
      'username': [''],
      'firstName': [''],
      'lastName': [''],
      'memberOnly': [true]
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.searchFormGroup.controls; }

  addUsers(e, element: UserPerDomainDto) {
    if (e['checked'])
      this.selectedUsers.push(element);
    else {
      const index = this.selectedUsers.indexOf(element, 0);
      if (index > -1) {
        this.selectedUsers.splice(index, 1);
      }
    }
    console.log(this.selectedUsers);
  }

  ok () {
    this.dialogRef.close(this.selectedUsers);
  }

  loadData() {
    // this.podiumService.load(
    //   this.podiumFormGroup.controls['date'].value,
    //   this.podiumFormGroup.controls['offset'].value,
    //   this.podiumFormGroup.controls['size'].value);
    this.searchUserService.load(this.offset, this.size,
      this.f['firstName'].value,
      this.f['lastName'].value,
      this.f['username'].value,
      this.f['memberOnly'].value,
      this.data['domain']['id']);
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.loadData();
  }
}
