import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import { EmployerListComponent } from './anas/components/employer-list/employer-list.component';
import { EmployabilityListComponent } from './anas/components/employability-list/employability-list.component';

import { EmployerFormComponent } from './anas/components/employer-form/employer-form.component';
import { EmployabilityFormComponent } from './anas/components/employability-form/employability-form.component';
import { EmployabilityStatsComponent } from './anas/components/employability-stats/employability-stats.component';
import { EmployerPotentialListComponent } from './anas/components/employer-potential-list/employer-potential-list.component';
import { EmployerEditComponent } from './anas/components/employer-edit/employer-edit.component';


export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'employer-form', component: EmployerFormComponent },
      { path: 'employers', component: EmployerListComponent },
      { path: 'employability', component: EmployabilityListComponent },
      { path: 'employability/stats', component: EmployabilityStatsComponent },
      { path: 'employability/add', component: EmployabilityFormComponent },
      { path: 'employers/potential', component: EmployerPotentialListComponent },
      { path: 'employers/edit/:id', component: EmployerEditComponent }

    ]
  },
];
