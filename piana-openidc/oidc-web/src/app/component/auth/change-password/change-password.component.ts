import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MyErrorStateMatcher } from '../../root/domains/domains.component';
import { AuthService } from '../auth.service';
import { MessageType, ShowMessageService } from '../../service/show-message/show-message.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule, MatInputModule
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  changePasswordFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();

  constructor(
    fb: FormBuilder,
    public authService: AuthService,
    private showMessageService: ShowMessageService) {

    this.changePasswordFormGroup = fb.group({
      'password': ['', Validators.required],
      'newPassword': ['', Validators.required],
      'confirmPassword': ['', Validators.required]
    });
  }

  changePassword() {
    console.log('changePassword', this.changePasswordFormGroup.valid)
    if (this.changePasswordFormGroup.controls['newPassword'].value !== this.changePasswordFormGroup.controls['confirmPassword'].value) {
      this.showMessageService.addMessage({
        messageType: MessageType.INFO,
        title: 'password failed',
        description: 'newPassword not equals to confirmPassword'
      })
    }
    if (this.changePasswordFormGroup.valid) {
      this.authService.changePassword(
        this.changePasswordFormGroup.controls['password'].value,
        this.changePasswordFormGroup.controls['newPassword'].value)
    }
  }
}
