import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { ShowMessageService } from '../../service/show-message/show-message.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ErrorStateMatcher } from '@angular/material/core';
import { MyErrorStateMatcher } from '../../root/domains/domains.component';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule, MatInputModule, MatSelectModule 
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginFormGroup: FormGroup;
  matcher = new MyErrorStateMatcher();

  constructor(
    fb: FormBuilder,
    public authService: AuthService,
    private showMessageService: ShowMessageService) {
    
    this.loginFormGroup = fb.group({
      'username': ['', Validators.required],
      'password': ['', Validators.required],
      'domainId': ['', Validators.required]
    });
  }

  login() {
    console.log('login', this.loginFormGroup.valid)
    if (this.loginFormGroup.valid) {
      this.authService.login(
        this.loginFormGroup.controls['username'].value, 
        this.loginFormGroup.controls['password'].value,
        this.loginFormGroup.controls['domainId'].value)
    }
  }
}
