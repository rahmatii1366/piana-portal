import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DomainsService } from '../domains.service';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MyErrorStateMatcher } from '../domains.component';
import { MatIconModule } from '@angular/material/icon';
import { SearchUserDialogComponent } from '../../modal/search-user-dialog/search-user-dialog.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { UserPerDomainDto } from '../../modal/search-user-dialog/search-user.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RolePerUserInDomainDto } from '../../modal/search-role-for-user-in-domain-dialog/search-role-for-user-in-domain.service';
import { SearchRoleForUserInDomainDialogComponent } from '../../modal/search-role-for-user-in-domain-dialog/search-role-for-user-in-domain-dialog.component';
import { lastValueFrom } from 'rxjs';
import { MessageType, ShowMessageService } from '../../../service/show-message/show-message.service';
import { AddUserIntoDomainDialogComponent } from '../add-user-into-domain-dialog/add-user-into-domain-dialog.component';
import { SelectRoleForDomainDialogComponent } from '../../modal/select-role-for-domain-dialog/select-role-for-domain-dialog.component';
import { SelectPermissionsForRoleInDomainDialogComponent } from '../../modal/select-permissions-for-role-in-domain-dialog/select-permissions-for-role-in-domain-dialog.component';
import { RoleDto } from '../../roles/roles.service';
import { PermissionDto } from '../../permissions/permissions.service';

@Component({
  selector: 'app-add-role-into-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatCheckboxModule, MatTooltipModule
  ],
  templateUrl: './add-role-into-domain-dialog.component.html',
  styleUrl: './add-role-into-domain-dialog.component.css'
})
export class AddRoleIntoDomainDialogComponent {
  readonly dialogRef = inject(MatDialogRef<AddRoleIntoDomainDialogComponent>);
  readonly dialog = inject(MatDialog);
  data = inject(MAT_DIALOG_DATA);

  newDomainFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  selectRoleButtonEnable: boolean = true;
  public selectedPermissions: PermissionDto[] = [];
  selectedRole: RolePerUserInDomainDto = null;

  constructor(fb: FormBuilder, public domainService: DomainsService, private showMessage: ShowMessageService) {
    console.log(this.data)
    if (this.data['role']) {
      this.newDomainFormGroup = fb.group({
        'role': [{ value: this.data['role']['name'], disabled: true }, Validators.required],
        'permissions': [{ value: '', disabled: true }, Validators.required]
      });
      this.selectRoleButtonEnable = false;
    } else {
      this.newDomainFormGroup = fb.group({
        'role': [{ value: '', disabled: true }, Validators.required],
        'permissions': [{ value: '', disabled: true }, Validators.required]
      });
    }
    
  }

  async insertData() {
    try {

      let a$ = this.domainService.addRoleByPermissions(this.data['domain'].id, this.selectedRole.id, this.selectedPermissions.map(p => p.id));
      let res = await lastValueFrom(a$);

      if (res.status == 200) {
        this.showMessage.addMessage({
          messageType: MessageType.SUCCESS,
          title: "success",
          description: "add role by permissions successfully"
        });
        this.dialogRef.close(this.selectedRole);
      } else {
        console.log('error');
        this.showMessage.addMessage({
          messageType: MessageType.ERROR,
          title: "error",
          description: "add users by roles failed"
        });
      }

    } catch (e) {
    }
  }

  get f() {
    return this.newDomainFormGroup.controls;
  }

  openSelectRoleDialog(event) {
    this.selectedRole = null;

    const dialogRef = this.dialog.open(SelectRoleForDomainDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: this.data['domain'] }
    });
    this.f['role'].setValue('');

    dialogRef.afterClosed().subscribe(result => {
      this.selectedRole = result;
      this.f['role'].setValue(this.selectedRole['name']);
    });

    event.stopPropagation();
  }

  openSeachPermissionsDialog(event) {
    this.selectedPermissions = [];

    const dialogRef = this.dialog.open(SelectPermissionsForRoleInDomainDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: this.data['domain'], role: this.selectedRole }
    });
    this.f['permissions'].setValue('');

    dialogRef.afterClosed().subscribe(result => {
      this.selectedPermissions = result;
      
      let permissions: string = '';
      for (let i = 0; i < result.length; i++) {
        permissions += result[i].name;
        if (i < result.length - 1)
          permissions += ',';
        if (permissions.length > 30) {
          permissions += '...';
          break;
        }
      }

      this.f['permissions'].setValue(permissions);
    });

    event.stopPropagation();
  }
}
