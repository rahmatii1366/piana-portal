import { Component, Inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ThemeComponent } from "../shared/theme/theme.component";
import { HeaderComponent } from "../shared/header/header.component";
import { AsyncPipe, DOCUMENT } from '@angular/common';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeComponent, HeaderComponent, AsyncPipe],
  templateUrl: './root.component.html',
  styleUrl: './root.component.css'
})
export class RootComponent {
constructor(@Inject(DOCUMENT) private document: Document,
  public authService: AuthService) {}

  ngOnInit() {
    this.document.body.classList.remove('d-flex');
    this.document.body.classList.remove('align-items-center');
    this.document.body.classList.remove('py-4');
    this.document.body.classList.remove('bg-body-tertiary');
  }

  hasPermission(perm): boolean {
    // console.log(perm)
    return this.authService.hasPermissionFormUI(perm);
    // return this.authService.getUIPermissionId(perm) > 0;
    
    // if (!this.perms.includes(perm)) {
    //   this.perms.push(perm)    
    //   let data = this.authService.hasPermissionFormUI(perm);
    //   console.log(data);
    //   return true;
    // }
    // return false;
  }

}
