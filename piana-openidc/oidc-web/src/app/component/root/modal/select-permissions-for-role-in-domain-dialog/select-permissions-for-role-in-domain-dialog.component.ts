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
import { MyErrorStateMatcher } from '../../domains/domains.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-select-permissions-for-role-in-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatPaginatorModule, MatTableModule,
    MatDialogTitle, MatDialogContent, MatTooltipModule, MatCheckboxModule
  ],
  templateUrl: './select-permissions-for-role-in-domain-dialog.component.html',
  styleUrl: './select-permissions-for-role-in-domain-dialog.component.css'
})
export class SelectPermissionsForRoleInDomainDialogComponent {
  readonly dialogRef = inject(MatDialogRef<SelectPermissionsForRoleInDomainDialogComponent>);
  data = inject(MAT_DIALOG_DATA);
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'name', 'description'];
  displayedColumnsWithCommand: string[] = [...this.displayedColumns, 'operation'];
  size: number = 25;
  offset: number = 0;
  public dialogData: any;

  public selectedPermissions: PermissionDto[] = [];
  public loaded: PermissionsResponseDto = {
    count: 0,
    data: []
  }

  constructor(fb: FormBuilder, private http: HttpClient) {
    console.log('data', this.data)
    this.searchFormGroup = fb.group({
      'name': ['']
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.searchFormGroup.controls; }

  addRoles(e, element: PermissionDto) {
    if (e['checked'])
      this.selectedPermissions.push(element);
    else {
      const index = this.selectedPermissions.indexOf(element, 0);
      if (index > -1) {
        this.selectedPermissions.splice(index, 1);
      }
    }
  }

  ok() {
    this.dialogRef.close(this.selectedPermissions);
  }

  loadData() {
    this.http.get('oidc-ui/api/v1/piana/oidc/permission/for-role-in-domain', { observe: 'response', params: { name: this.f['name'].value, offset: this.offset, size: this.size } })
    .subscribe({
      next: res => {
        if (res.status == 200) {
          this.loaded = { count: res.body['count'], data: res.body['data'] };
        } else {
          console.log('error');
        }
      }, error: err => {
        console.log('error =>', err);
      }, complete: () => {
      }
    });
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.loadData();
  }
}

export interface PermissionsResponseDto {
  count: number,
  data: PermissionDto[]
}

export interface PermissionDto {
  id: number,
  name: string,
  domainId: number
}
