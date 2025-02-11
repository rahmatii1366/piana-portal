import { Component } from '@angular/core';
import { ClientGroupService } from './service/client-group.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'oidc-client-config';
  
  constructor(public clientGroupService: ClientGroupService) {
    try {
       clientGroupService.getAll();
    } catch (e) {
      console.log(e);
    }
  }
}
