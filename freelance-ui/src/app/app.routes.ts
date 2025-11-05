import { Routes } from '@angular/router';
import {HomeComponent} from './component/home/home.component';
import {UserLayoutComponent} from './layouts/user-layout/user-layout.component';
import {ListFreelancersComponent} from './louay/pages/list-freelancers/list-freelancers.component';
import {AddFreelancersComponent} from './louay/pages/add-freelancers/add-freelancers.component';
import {UpdateFreelancersComponent} from './louay/pages/update-freelancers/update-freelancers.component';
import {SearchFreelancersComponent} from './louay/pages/search-freelancers/search-freelancers.component';
import {RecommendFreelancersComponent} from './louay/pages/recommend-freelancers/recommend-freelancers.component';
import {StatsFreelancersComponent} from './louay/pages/stats-freelancers/stats-freelancers.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'freelance/list', component: ListFreelancersComponent },
      { path: 'freelance/add', component: AddFreelancersComponent },
      { path: 'freelance/update/:id', component: UpdateFreelancersComponent },
      { path: 'freelance/search', component: SearchFreelancersComponent },
      { path: 'freelance/recommand', component: RecommendFreelancersComponent },
      { path: 'freelance/stats', component: StatsFreelancersComponent },
      // Ajoutez d'autres routes ici pour les pages utilisateur
    ]
  },
];
