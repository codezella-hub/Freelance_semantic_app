import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployerService } from '../../services/employer.service';
import { EmployabilityService } from '../../services/employability.service';
import { Employer } from '../../models/employer.model';
import { Employability } from '../../models/employability.model';

@Component({
  selector: 'app-employer-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employer-edit.component.html',
  styleUrls: ['./employer-edit.component.scss']
})
export class EmployerEditComponent implements OnInit {
  employer: Employer = {
    id: '',
    type: 'Company',
    companyName: '',
    email: '',
    phoneNumber: '',
    employabilityId: ''
  };

  employabilities: Employability[] = [];
  loading = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private api: EmployerService,
    private employabilityApi: EmployabilityService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadEmployabilities();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.loadEmployer(id);
  }

  loadEmployabilities() {
    this.employabilityApi.list().subscribe({
      next: (res) => (this.employabilities = res),
      error: (err) => console.error('Erreur de chargement des employabilités', err)
    });
  }

  loadEmployer(id: string) {
    this.loading = true;
    this.api.get(id).subscribe({
      next: (res) => {
        this.employer = res;
        this.loading = false;
      },
      error: () => {
        this.errorMsg = '❌ Impossible de charger les données de cet employeur.';
        this.loading = false;
      }
    });
  }

  onSubmit() {
    this.loading = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.api.update(this.employer.id!, this.employer).subscribe({
      next: (res) => {
        this.successMsg = `✅ Employeur "${res.companyName}" mis à jour avec succès !`;

        setTimeout(() => {
          this.router.navigate(['/employers']);
        }, 1200);

        this.loading = false;
      },
      error: () => {
        this.errorMsg = '❌ Erreur lors de la mise à jour. Veuillez réessayer.';
        this.loading = false;
      }
    });
  }
}
