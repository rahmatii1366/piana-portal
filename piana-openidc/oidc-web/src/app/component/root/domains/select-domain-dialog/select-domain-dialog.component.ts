import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ShowMessageService } from '../../../service/show-message/show-message.service';
import { DomainDto } from '../domains.service';
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-select-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatCheckboxModule, MatTooltipModule,
    MatListModule
  ],
  templateUrl: './select-domain-dialog.component.html',
  styleUrl: './select-domain-dialog.component.css'
})
export class SelectDomainDialogComponent implements OnInit {
  readonly dialogRef = inject(MatDialogRef<SelectDomainDialogComponent>);
  readonly dialog = inject(MatDialog);
  data = inject(MAT_DIALOG_DATA);

  form: FormGroup;
  domains: DomainForUserDto[] = []

  constructor(
    fb: FormBuilder,
    private http: HttpClient,
    private showMessage: ShowMessageService) {
    this.form = fb.group({
      'domainId': ['', Validators.required]
    });
  }

  ngOnInit() {
    this.http.get('oidc-ui/api/v1/piana/oidc/domain/for-user', { observe: 'response', params: { offset: 0, size: 100000, userId: this.data['user']['id'] } })
      .subscribe({
        next: res => {
          if (res.status == 200) {
            this.domains = res.body['data'];
          } else {
            console.log('error');
          }
        }, error: err => {
          console.log('error =>', err);
        }, complete: () => {
        }
      });
  }

  ok() {
    let domain = this.domains.find(item => item.id === this.form.controls['domainId'].value[0]);
    // console.log(this.domains, domain, this.form.controls['domainId'].value);
    this.dialogRef.close(domain);
  }
}

export interface DomainForUserDto {
  id: number,
  name: string,
  description: string,
  assigned: boolean,
}
