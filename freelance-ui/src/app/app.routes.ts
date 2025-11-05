import { Routes } from '@angular/router';
import { HomeComponent } from './component/home/home.component';
import { UserLayoutComponent } from './layouts/user-layout/user-layout.component';
import { ContractsComponent } from './contracts/contracts.component';
import { PaymentsComponent } from './payments/payments.component';
import { AddContractComponent } from './add-contract/add-contract.component';
import { AddPaymentComponent } from './add-payment/add-payment.component';
import { SearchComponent } from './search/search.component';

export const routes: Routes = [
  {
    path: '',
    component: UserLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
  { path: 'contracts', component: ContractsComponent },
  { path: 'contracts/add', component: AddContractComponent },
  { path: 'contracts/edit/:id', component: AddContractComponent },
  { path: 'payments', component: PaymentsComponent },
  { path: 'payments/add', component: AddPaymentComponent },
  { path: 'payments/edit/:id', component: AddPaymentComponent },
  { path: 'search', component: SearchComponent },
      // add more routes here if needed
    ]
  }
];
