import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EvaluationService, Evaluation } from '../evaluation.service';

@Component({
  selector: 'app-evaluation-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evaluation-form.component.html',
  styleUrls: ['./evaluation-form.component.scss'],
})
export class EvaluationFormComponent {
  evaluation: Evaluation = {
    type: 'ClientReview',
    score: 0,
    comment: '',
    evaluationDate: '',
  };

  message = '';

  constructor(
    private evaluationService: EvaluationService,
    private router: Router
  ) {}

  // ➕ Soumettre le formulaire
  onSubmit(): void {
    if (!this.evaluation.comment || !this.evaluation.score) {
      this.message = '⚠️ Veuillez remplir tous les champs obligatoires.';
      return;
    }

    // Format de date ISO pour le backend
    this.evaluation.evaluationDate = new Date().toISOString();

    this.evaluationService.create(this.evaluation).subscribe({
      next: () => {
        this.message = '✅ Évaluation ajoutée avec succès !';
        setTimeout(() => this.router.navigate(['/evaluations']), 1500);
      },
      error: (err) => {
        console.error('Erreur lors de la création :', err);
        this.message = '❌ Erreur lors de la création.';
      },
    });
  }
}
