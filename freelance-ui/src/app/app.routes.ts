import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import {EventListComponent} from './component/event-list/event-list.component';
import {EventFormComponent} from './component/event-form/event-form.component';
import {EventDetailComponent} from './component/event-detail/event-detail.component';
import {CertificationListComponent} from './component/certification-list/certification-list.component';
import {CertificationFormComponent} from './component/certification-form/certification-form.component';
import {CertificationDetailComponent} from './component/certification-detail/certification-detail.component';
import {NlpSearchComponent} from './component/nlp-search/nlp-search.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'search', component: NlpSearchComponent },
      { path: 'events', component: EventListComponent },
      { path: 'events/new', component: EventFormComponent },
      { path: 'events/view/:uri', component: EventDetailComponent },
      { path: 'events/edit/:uri', component: EventFormComponent },
      { path: 'certifications', component: CertificationListComponent },
      { path: 'certifications/new', component: CertificationFormComponent },
      { path: 'certifications/view/:uri', component: CertificationDetailComponent },
      { path: 'certifications/edit/:uri', component: CertificationFormComponent },
      // Ajoutez d'autres routes ici pour les pages utilisateur
    ]
  },
];
