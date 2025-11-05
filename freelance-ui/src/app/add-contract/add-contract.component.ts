import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { RdfApiService } from '../services/rdf-api.service';

@Component({
  selector: 'app-add-contract',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-contract.component.html',
  styleUrls: ['./add-contract.component.scss']
})
export class AddContractComponent {
  model: any = {
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    amount: null,
    status: 'DRAFT'
  };

  editingId: number | null = null;

  statuses = ['DRAFT','ACTIVE','COMPLETED','CANCELLED'];

  constructor(private api: RdfApiService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editingId = Number(idParam);
      this.api.getContractById(this.editingId).subscribe({
        next: (res: any) => {
          // populate model from JPA contract
          this.model.title = res.title;
          this.model.description = res.description;
          this.model.startDate = res.startDate || '';
          this.model.endDate = res.endDate || '';
          this.model.amount = res.amount;
          this.model.status = res.status || 'DRAFT';
        },
        error: err => console.error('Failed to load contract', err)
      });
    }
  }

  submit() {
    // convert dates to ISO date strings acceptable by backend (LocalDate)
    const payload = {
      title: this.model.title,
      description: this.model.description,
      startDate: this.model.startDate || null,
      endDate: this.model.endDate || null,
      amount: this.model.amount,
      status: this.model.status
    };

    if (this.editingId) {
      this.api.updateContract(this.editingId, payload).subscribe({
        next: () => this.router.navigate(['/contracts']),
        error: err => console.error('Update contract failed', err)
      });
    } else {
      this.api.createContract(payload).subscribe({
        next: () => this.router.navigate(['/contracts']),
        error: err => console.error('Create contract failed', err)
      });
    }
  }
}
