import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EmployerService } from '../../services/employer.service';
import { EmployabilityService } from '../../services/employability.service'; 
import { Employer } from '../../models/employer.model';
import { Employability } from '../../models/employability.model';

@Component({
  selector: 'app-employer-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employer-form.component.html',
  styleUrls: ['./employer-form.component.scss']
})
export class EmployerFormComponent implements OnInit {
  employer: Employer = {
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
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEmployabilities();
  }

  
  loadEmployabilities() {
    this.employabilityApi.list().subscribe({
      next: (res) => (this.employabilities = res),
      error: (err) => console.error('Erreur de chargement des employabilités', err)
    });
  }

  onSubmit() {
    this.loading = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.api.create(this.employer).subscribe({
      next: (res) => {
        this.successMsg = `✅ Employeur "${res.companyName}" ajouté avec succès !`;

        setTimeout(() => {
          this.router.navigate(['/employers']);
        }, 1200);

        this.loading = false;
      },
      error: () => {
        this.errorMsg = '❌ Erreur lors de la création. Veuillez réessayer.';
        this.loading = false;
      }
    });
  }
}
