import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjetsService, Projet } from '../projets.service';
import { EvaluationService, Evaluation } from '../evaluation.service'; // ✅ pour charger les évaluations existantes

@Component({
  selector: 'app-projets-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projets-list.component.html',
  styleUrls: ['./projets-list.component.scss'],
})
export class ProjetsListComponent implements OnInit {
  projets: Projet[] = [];
  evaluations: Evaluation[] = []; // ⚡ Liste des évaluations déjà existantes
  showForm = false;

  newProjet: Projet = {
    type: 'OngoingProject',
    projectTitle: '',
    projectSummary: '',
    deliveryDate: '',
    evaluations: [],
  };

  constructor(
    private projetsService: ProjetsService,
    private evaluationService: EvaluationService
  ) {}

  ngOnInit(): void {
    this.loadProjets();
    this.loadEvaluations();
  }

  loadProjets(): void {
    this.projetsService.getAll().subscribe({
      next: (data) => (this.projets = data),
      error: (err) => console.error('Erreur chargement projets:', err),
    });
  }

  loadEvaluations(): void {
    this.evaluationService.getAll().subscribe({
      next: (data) => (this.evaluations = data),
      error: (err) => console.error('Erreur chargement évaluations:', err),
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  createProjet(): void {
    this.projetsService.create(this.newProjet).subscribe({
      next: () => {
        alert('Projet ajouté avec succès ✅');
        this.loadProjets();
        this.toggleForm();
      },
      error: (err) => console.error('Erreur création projet:', err),
    });
  }

  deleteProjet(id: string): void {
    if (confirm('Voulez-vous vraiment supprimer ce projet ?')) {
      this.projetsService.delete(id).subscribe({
        next: () => this.loadProjets(),
        error: (err) => console.error('Erreur suppression projet:', err),
      });
    }
  }
}
