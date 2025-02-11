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

@Component({
  selector: 'app-new-domain-dialog',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatIconModule, MatExpansionModule, MatCheckboxModule, MatTooltipModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './add-user-into-domain-dialog.component.html',
  styleUrl: './add-user-into-domain-dialog.component.css'
})
export class AddUserIntoDomainDialogComponent {
  readonly dialogRef = inject(MatDialogRef<AddUserIntoDomainDialogComponent>);
  readonly dialog = inject(MatDialog);
  data = inject(MAT_DIALOG_DATA);

  newDomainFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  public selectedUsers: UserPerDomainDto[] = [];
  selectUserButtonEnable: boolean = true;
  public selectedRoles: RolePerUserInDomainDto[] = [];


  constructor(fb: FormBuilder, public domainService: DomainsService, private showMessage: ShowMessageService) {
    console.log(this.data)
    if (this.data['user']) {
      this.newDomainFormGroup = fb.group({
        'users': [{ value: this.data['user']['username'], disabled: true }, Validators.required],
        'roles': [{ value: '', disabled: true }, Validators.required]
      });
      this.selectUserButtonEnable = false;
      this.selectedUsers.push(this.data['user']);
    } else {
      this.newDomainFormGroup = fb.group({
        'users': [{ value: '', disabled: true }, Validators.required],
        'roles': [{ value: '', disabled: true }, Validators.required]
      });
    }
    
  }

  async insertData() {
    try {

      let a$ = this.domainService.addUsersByRoles(this.data['domain'].id, this.selectedUsers.map(u => u.id), this.selectedRoles.map(r => r.id));
      let res = await lastValueFrom(a$);

      if (res.status == 200) {
        this.showMessage.addMessage({
          messageType: MessageType.SUCCESS,
          title: "success",
          description: "add users by roles successfully"
        });
        this.dialogRef.close(this.selectedUsers);
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

  openSeachUserDialog() {
    this.selectedUsers = [];

    const dialogRef = this.dialog.open(SearchUserDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: this.data['domain'] }
    });
    this.f['users'].setValue('');

    dialogRef.afterClosed().subscribe(result => {
      this.selectedUsers = result;

      let users: string = '';
      for (let i = 0; i < result.length; i++) {
        users += result[i].username;
        if (i < result.length - 1)
          users += ',';
      }

      this.f['users'].setValue(users);
    });
  }

  openSeachRoleDialog(event) {
    this.selectedRoles = [];

    const dialogRef = this.dialog.open(SearchRoleForUserInDomainDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: this.data['domain'] }
    });
    this.f['roles'].setValue('');

    dialogRef.afterClosed().subscribe(result => {
      this.selectedRoles = result;

      let roles: string = '';
      for (let i = 0; i < result.length; i++) {
        roles += result[i].name;
        if (i < result.length - 1)
          roles += ',';
      }

      this.f['roles'].setValue(roles);
    });

    event.stopPropagation();
  }
}
