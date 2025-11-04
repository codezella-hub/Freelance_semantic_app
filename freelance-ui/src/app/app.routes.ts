import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import { MissionsComponent } from './components/missions/missions.component';
import { ApplicationsComponent } from './components/applications/applications.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'missions', component: MissionsComponent },
      { path: 'applications', component: ApplicationsComponent }
    ]
  },
];
