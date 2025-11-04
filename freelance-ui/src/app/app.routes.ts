import { Routes } from '@angular/router';
import { HomeComponent } from './component/home/home.component';
import { UserLayoutComponent } from './layouts/user-layout/user-layout.component';
import { EvaluationListComponent } from './chaima/evaluation-list/evaluation-list.component';
import { EvaluationFormComponent } from './chaima/evaluation-form/evaluation-form.component';
import { ProjetsListComponent } from './chaima/projets-list/projets-list.component';
import { ProjetDashboardComponent } from './chaima/projet-dashboard/projet-dashboard.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'evaluations', component: EvaluationListComponent },
      { path: 'evaluations/new', component: EvaluationFormComponent },
      // Ajoutez d'autres routes ici pour les pages utilisateur
      { path: 'projets', component: ProjetsListComponent },
      { path: 'projets-dashboard', component: ProjetDashboardComponent },
    ],
  },
];
