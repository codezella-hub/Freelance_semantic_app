import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import { MissionsComponent } from './components/missions/missions.component';
import { ApplicationsComponent } from './components/applications/applications.component';
import {EventListComponent} from './component/event-list/event-list.component';
import {EventFormComponent} from './component/event-form/event-form.component';
import {EventDetailComponent} from './component/event-detail/event-detail.component';
import {CertificationListComponent} from './component/certification-list/certification-list.component';
import {CertificationFormComponent} from './component/certification-form/certification-form.component';
import {CertificationDetailComponent} from './component/certification-detail/certification-detail.component';
import {NlpSearchComponent} from './component/nlp-search/nlp-search.component';
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

      { path: 'missions', component: MissionsComponent },
      { path: 'applications', component: ApplicationsComponent },
      { path: 'search', component: NlpSearchComponent },
      { path: 'events', component: EventListComponent },
      { path: 'events/new', component: EventFormComponent },
      { path: 'events/view/:uri', component: EventDetailComponent },
      { path: 'events/edit/:uri', component: EventFormComponent },
      { path: 'certifications', component: CertificationListComponent },
      { path: 'certifications/new', component: CertificationFormComponent },
      { path: 'certifications/view/:uri', component: CertificationDetailComponent },
      { path: 'certifications/edit/:uri', component: CertificationFormComponent },

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
