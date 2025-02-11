import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, FormsModule, NgForm, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { DomainDto, DomainsService } from './domains.service';
import { MatPaginatorIntlCroService } from '../../shared/mat-paginator-intl-cro/mat-paginator-intl-cro.service';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AddUserIntoDomainDialogComponent } from './add-user-into-domain-dialog/add-user-into-domain-dialog.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { MatButtonModule, MatIconButton } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AddRoleIntoDomainDialogComponent } from './add-role-into-domain-dialog/add-role-into-domain-dialog.component';
import { CommonQueryService, RoleInDomainDto, RoleInDomainRequestDto } from '../../service/common-query/common-query.service';

@Component({
  selector: 'app-domains',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatPaginatorModule, MatFormFieldModule, MatInputModule, MatTableModule, MatDialogModule,
    MatExpansionModule, MatIconModule, MatButtonModule, MatAutocompleteModule, MatTooltipModule
  ],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './domains.component.html',
  styleUrl: './domains.component.css',
  providers: [
    DomainsService,
    { provide: MatPaginatorIntl, useClass: MatPaginatorIntlCroService }
  ]
})
export class DomainsComponent {
  readonly dialog = inject(MatDialog);

  newDomainFormGroup: FormGroup;
  searchFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();
  displayedColumns: string[] = ['id', 'name', 'description', 'operations'];
  displayedColumnsWithExpand = [...this.displayedColumns, 'expand'];

  size: number = 25;
  offset: number = 0;

  expandedElement: DomainDto = null;

  constructor(
    private commonQuery: CommonQueryService,
    fb: FormBuilder,
    public domainService: DomainsService,
    private showMessageService: ShowMessageService) {
    this.searchFormGroup = fb.group({
      'domainName': [''],
      // 'date': ['', Validators.required],
      // 'minAmount': [null, Validators.min(0)],
      // 'maxAmount': [null]
    },
      // 👈 after we have finished set the controls we add formGroup validators
      // {  
      //   validators: [maxAmountValidator] 
      // }
    );
    this.newDomainFormGroup = fb.group({
      'domainName': ['', Validators.required],
      'domainDescription': ['']
    });
  }

  public domainRoles: RoleInDomainRequestDto = {
    count: 0,
    data: []
  };

  async selectDomain(event, row) {
    this.expandedElement = this.expandedElement === row ? null : row;

    try {
      let res = await this.commonQuery.getRolesForDomain(row['id'], 0, 100);

      if (res.status == 200) {
        this.domainRoles = { count: res.body['count'], data: res.body['data'] };
      } else {
        console.log('error');
      }
    } catch (e) {
      console.log('error', e)
    }
    event.stopPropagation();
  }

  openAddUserDialog(event, domain) {
    console.log(domain)
    const dialogRef = this.dialog.open(AddUserIntoDomainDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: domain }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });

    event.stopPropagation();
  }

  openAddRoleDialog(event, domain) {
    console.log(domain)
    const dialogRef = this.dialog.open(AddRoleIntoDomainDialogComponent, {
      height: '80%',
      width: '80%',
      disableClose: true,
      data: { domain: domain }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });

    event.stopPropagation();
  }

  loadData() {
    // this.podiumService.load(
    //   this.podiumFormGroup.controls['date'].value,
    //   this.podiumFormGroup.controls['offset'].value,
    //   this.podiumFormGroup.controls['size'].value);
    this.domainService.load(this.offset, this.size);
  }

  insert() {
    if (this.newDomainFormGroup.valid) {
      console.log('insert');
      this.domainService.create(
        this.newDomainFormGroup.controls['domainName'].value,
        this.newDomainFormGroup.controls['domainDescription'].value);
    }
  }

  addMessage() {
    this.showMessageService.addMessage({ messageType: MessageType.SUCCESS, title: 'a1', description: 'a2' })
  }

  handlePage(e: any) {
    console.log(e.pageIndex, e.pageSize)
    this.offset = e.pageIndex * e.pageSize;
    this.size = e.pageSize;
    this.domainService.load(this.offset, this.size);
  }
}


/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

// export function maxAmountValidator(fb: FormGroup): ValidationErrors | null {
//   const minAmount = +(fb.get("minAmount").value || '0');
//   const maxAmount = +(fb.get("maxAmount").value || '0');

//   if (maxAmount < minAmount) {
//     return {
//       maxAmount:
//       {
//         minAmount,
//         maxAmount
//       }
//     }
//   } else {
//     null
//   }

//   return null;

// }