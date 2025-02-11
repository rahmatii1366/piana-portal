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
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { RoleInDomainDto, RoleInDomainRequestDto } from '../../../service/common-query/common-query.service';

@Component({
  selector: 'app-select-role-for-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatPaginatorModule, MatTableModule,
    MatDialogTitle, MatDialogContent, MatTooltipModule, MatCheckboxModule,
    MatRadioModule, MatSelectModule
  ],
  templateUrl: './select-role-for-domain-dialog.component.html',
  styleUrl: './select-role-for-domain-dialog.component.css'
})
export class SelectRoleForDomainDialogComponent {
  readonly dialogRef = inject(MatDialogRef<SelectRoleForDomainDialogComponent>);
  data = inject(MAT_DIALOG_DATA);
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'name', 'description'];
  displayedColumnsWithCommand: string[] = [...this.displayedColumns, 'operation'];
  size: number = 25;
  offset: number = 0;
  public dialogData: any;

  public selectedRole: RoleInDomainDto = null;
  public roles: RoleInDomainRequestDto = {
    count: 0,
    data: []
  };


  constructor(fb: FormBuilder, private http: HttpClient) {
    console.log('data', this.data)
    this.searchFormGroup = fb.group({
      'name': [''],
      'assigned': [null]
    });
  }

  // convenience getter for easy access to form fields
  get f() { return this.searchFormGroup.controls; }

  selectRole(e, element: RoleInDomainDto) {
    if (e['checked'])
      this.selectedRole = element;
    else {
      const index = this.selectedRole = null;
    }
    console.log(this.selectedRole);
  }

  ok() {
    this.dialogRef.close(this.selectedRole);
  }

  loadData() {
    this.http.get('oidc-ui/api/v1/piana/oidc/role/for-domain', {
      observe: 'response', params: {
        domainId: this.data['domain']['id'], name: this.f['name'].value, memberOnly: this.f['assigned'].value == null ? '' : this.f['assigned'].value, offset: 0, size: 1000
      }
    })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.roles = { count: res.body['count'], data: res.body['data'] };
            console.log(res.body, this.roles)
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });

    // this.podiumService.load(
    //   this.podiumFormGroup.controls['date'].value,
    //   this.podiumFormGroup.controls['offset'].value,
    //   this.podiumFormGroup.controls['size'].value);
    // this.searchRoleService.load(this.offset, this.size,
    //   this.f['name'].value);
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    // this.searchRoleService.load(this.offset, this.size,
    //   this.f['name'].value);
  }
}

