import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';   // ✅ Import
import { EmployabilityService } from '../../services/employability.service';
import { Employability } from '../../models/employability.model';

@Component({
  selector: 'app-employability-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './employability-form.component.html',
  styleUrls: ['./employability-form.component.scss']
})
export class EmployabilityFormComponent {
  employability: Employability = {
    kind: 'ExperienceLevel',
    employabilityScore: 0
  };

  successMsg = '';
  errorMsg = '';
  loading = false;

  constructor(private api: EmployabilityService, private router: Router) {} 

  onSubmit() {
    this.loading = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.api.create(this.employability).subscribe({
      next: (res) => {
        this.successMsg = `✅ Employabilité ajoutée avec succès (score ${res.employabilityScore}).`;

        setTimeout(() => {
          this.router.navigate(['/employability']); 
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
