import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      // Ajoutez d'autres routes ici pour les pages utilisateur
    ]
  },
];
