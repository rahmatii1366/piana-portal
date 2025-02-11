import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, FormsModule, NgForm, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorIntlCroService } from '../../shared/mat-paginator-intl-cro/mat-paginator-intl-cro.service';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';
import { MyErrorStateMatcher } from '../domains/domains.component';
import { UserDto, UsersService } from './users.service';
import { MatButtonModule } from '@angular/material/button';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { UserDomainService } from './user-domain.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Clipboard, ClipboardModule } from '@angular/cdk/clipboard';
import { SelectDomainDialogComponent } from '../domains/select-domain-dialog/select-domain-dialog.component';
import { AddUserIntoDomainDialogComponent } from '../domains/add-user-into-domain-dialog/add-user-into-domain-dialog.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatPaginatorModule, MatFormFieldModule, MatInputModule, MatTableModule, MatTooltipModule,
    MatDialogModule, MatExpansionModule, MatIconModule, MatButtonModule, ClipboardModule
  ],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css',
  providers: [
    UsersService,
    UserDomainService,
    { provide: MatPaginatorIntl, useClass: MatPaginatorIntlCroService }
  ]
})
export class UsersComponent {
  readonly dialog = inject(MatDialog);

  insertFormGroup: FormGroup;
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'username', 'operations'];
  size: number = 10;
  offset: number = 0;

  expandedElement: UserDto = null;

  constructor(
    private clipboard: Clipboard,
    fb: FormBuilder,
    public userService: UsersService,
    public userDomainService: UserDomainService,
    private showMessageService: ShowMessageService) {
    this.searchFormGroup = fb.group({
      'firstName': ['']
    });

    this.insertFormGroup = fb.group({
      'firstName': ['', Validators.required],
      'lastName': ['', Validators.required],
      'username': ['', Validators.required],
      'password': ['', Validators.required]
    });
  }

  // openNewDialog() {
  //   const dialogRef = this.dialog.open(NewUserDialogComponent, {
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
    this.userService.load(this.offset, this.size);
  }

  insert() {
    if (this.insertFormGroup.valid) {
      console.log('insert');
      this.userService.create(
        this.insertFormGroup.controls['firstName'].value,
        this.insertFormGroup.controls['lastName'].value,
        this.insertFormGroup.controls['username'].value,
        this.insertFormGroup.controls['username'].value
      );
    }
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.userService.load(this.offset, this.size);
  }

  reloadDomainsAndItsRoles(user: UserDto) {
    this.userDomainService.reload(user.id);
  }

  openDomains(row) {
    this.expandedElement = this.expandedElement === row ? null : row;
    if (this.expandedElement == row) {
      this.userDomainService.reload(row['id'])
    }
    // 
  }

  public hide = true;

  generatePassword() {
    let generatedPassword = makeid(8);
    this.insertFormGroup.controls['password'].setValue(generatedPassword);
  }

  copyToClipboardPassword() {
    this.clipboard.copy(this.insertFormGroup.controls['password'].value);
    this.showMessageService.addMessage({
      messageType: MessageType.INFO,
      title: 'copy',
      description: 'copy to clipboard'
    });
  }

  openAddToDomainDialog(event, user: UserDto) {
    const dialogRef = this.dialog.open(SelectDomainDialogComponent, {
      height: '60%',
      width: '60%',
      disableClose: true,
      data: { user: user }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('selectDomainDialog closed!', result)

      if (result && result['id']) {
        this.dialog.open(AddUserIntoDomainDialogComponent, {
          height: '80%',
          width: '80%',
          disableClose: true,
          data: { domain: result, user: user }
        })
      }
    });

    event.stopPropagation();
  }
}

export function makeid(length) {
  let result = '';
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}