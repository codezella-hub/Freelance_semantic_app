import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EvaluationService, Evaluation } from '../evaluation.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-evaluation-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './evaluation-list.component.html',
  styleUrls: ['./evaluation-list.component.scss'],
})
export class EvaluationListComponent implements OnInit {
  evaluations: Evaluation[] = [];
  searchKeyword: string = '';
  sortBy: string = 'score';
  sortOrder: string = 'asc';

  constructor(private evaluationService: EvaluationService) {}

  ngOnInit(): void {
    this.loadEvaluations();
  }

  // 🔹 Charger toutes les évaluations
  loadEvaluations(): void {
    this.evaluationService.getAll().subscribe({
      next: (data) => (this.evaluations = data),
      error: (err) => console.error('Erreur lors du chargement :', err),
    });
  }

  // 🔍 Rechercher
  search(): void {
    if (!this.searchKeyword.trim()) {
      this.loadEvaluations(); // ✅ correction ici
      return;
    }

    this.evaluationService.search(this.searchKeyword).subscribe({
      next: (data) => (this.evaluations = data),
      error: (err) => console.error('Erreur lors de la recherche:', err),
    });
  }

  // 🔽 Trier
  sort(): void {
    this.evaluationService.sort(this.sortBy, this.sortOrder).subscribe({
      next: (data) => (this.evaluations = data),
      error: (err) => console.error('Erreur tri :', err),
    });
  }

  // ❌ Supprimer
  delete(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer cette évaluation ?')) {
      this.evaluationService.delete(id).subscribe({
        next: () => this.loadEvaluations(),
        error: (err) => console.error('Erreur suppression :', err),
      });
    }
  }
}
