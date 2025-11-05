import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { RdfApiService } from '../services/rdf-api.service';

@Component({
  selector: 'app-add-payment',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-payment.component.html',
  styleUrls: ['./add-payment.component.scss']
})
export class AddPaymentComponent {
  model: any = {
    contractId: null,
    amount: null,
    paymentDate: '',
    status: 'PENDING',
    paymentMethod: 'BANK_TRANSFER',
    description: ''
  };

  editingId: number | null = null;

  statuses = ['PENDING','COMPLETED','FAILED','REFUNDED'];
  methods = ['CREDIT_CARD','BANK_TRANSFER','PAYPAL','CRYPTO'];

  contracts: any[] = [];

  constructor(private api: RdfApiService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.api.getJpaContracts().subscribe((res: any) => {
      // res expected as array of contracts
      this.contracts = Array.isArray(res) ? res : [];
    });
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editingId = Number(idParam);
      this.api.getPaymentById(this.editingId).subscribe({
        next: (res: any) => {
          this.model.contractId = res.contract?.id || null;
          this.model.amount = res.amount;
          // Convert backend timestamp to datetime-local value if present
          this.model.paymentDate = res.paymentDate ? res.paymentDate.replace(':00.000','') : '';
          this.model.status = res.status || 'PENDING';
          this.model.paymentMethod = res.paymentMethod || 'BANK_TRANSFER';
          this.model.description = res.description || '';
        },
        error: err => console.error('Failed to load payment', err)
      });
    }
  }

  submit() {
    if (!this.model.contractId) return;
    // Build payload expected by backend: contract as object with id
    const payload: any = {
      contract: { id: this.model.contractId },
      amount: this.model.amount,
      paymentDate: this.model.paymentDate ? this.model.paymentDate : null,
      status: this.model.status,
      paymentMethod: this.model.paymentMethod,
      description: this.model.description
    };

    if (this.editingId) {
      this.api.updatePayment(this.editingId, payload).subscribe({
        next: () => this.router.navigate(['/payments']),
        error: err => console.error('Update payment failed', err)
      });
    } else {
      this.api.createPayment(payload).subscribe({
        next: () => this.router.navigate(['/payments']),
        error: err => console.error('Create payment failed', err)
      });
    }
  }
}
