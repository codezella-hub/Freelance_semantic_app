import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { RdfApiService } from '../services/rdf-api.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.scss']
})
export class PaymentsComponent {
  payments: any[] = [];

  constructor(private rdfService: RdfApiService, private router: Router) {}

  ngOnInit() {
    this.rdfService.getJpaPayments().subscribe((data: any) => {
      // JPA endpoint returns an array of Payment entities
      this.payments = Array.isArray(data) ? data : [];
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  extractPaymentId(uri: string): string {
    // If we get a URI-like id, try to return the fragment; otherwise return as-is
    if (!uri) return '';
    const parts = String(uri).split('#');
    return parts[parts.length - 1] || String(uri);
  }

  delete(id: number) {
    if (!confirm('Delete this payment?')) return;
    this.rdfService.deletePayment(id).subscribe({
      next: () => this.rdfService.getJpaPayments().subscribe((data: any) => this.payments = Array.isArray(data) ? data : []),
      error: err => console.error('Delete payment failed', err)
    });
  }
}
