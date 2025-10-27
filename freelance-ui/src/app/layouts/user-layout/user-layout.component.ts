import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {HeaderComponent} from '../../component/header/header.component';
import {FooterComponent} from '../../component/footer/footer.component';

@Component({
  selector: 'app-user-layout',
  imports: [
    RouterOutlet,
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './user-layout.component.html',
  standalone: true,
  styleUrl: './user-layout.component.scss'
})
export class UserLayoutComponent {

}
