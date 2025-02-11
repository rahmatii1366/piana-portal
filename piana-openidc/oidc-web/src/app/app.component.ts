import { Component, inject, Inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeComponent } from './component/shared/theme/theme.component';
import { HeaderComponent } from './component/shared/header/header.component';
import { AuthService } from './component/auth/auth.service';
import { AsyncPipe, DOCUMENT } from '@angular/common';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarModule, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MessageType, ShowMessageService } from './component/service/show-message/show-message.service';
import { take, timer } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ThemeComponent, HeaderComponent, AsyncPipe, MatSnackBarModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'oidc-ui';
  private _snackBar = inject(MatSnackBar);
  horizontalPosition: MatSnackBarHorizontalPosition = 'start';
  verticalPosition: MatSnackBarVerticalPosition = 'bottom';

  constructor(public authService: AuthService,
    @Inject(DOCUMENT) private document: Document,
    private showMessageService: ShowMessageService) {

    showMessageService.message.subscribe({
      next: (msg) => {
        if (msg != null) {
          const snackBarRef = this._snackBar.open(msg.description, msg.title, {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3 * 1000,
          });
  
          snackBarRef.afterDismissed().subscribe(info => {
            this.showMessageService.removeMessage();
            if (info.dismissedByAction === true) {
              // your code for handling this goes here
            }
          });
        }
      }
    });

    
  }

  ngOnInit() {
    this.document.body.classList.add('d-flex');
    this.document.body.classList.add('align-items-center');
    this.document.body.classList.add('py-4');
    this.document.body.classList.add('bg-body-tertiary');
  }
}
