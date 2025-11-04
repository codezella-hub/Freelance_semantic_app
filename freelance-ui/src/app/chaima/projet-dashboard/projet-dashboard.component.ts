import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';
import { Projet, ProjetsService } from '../projets.service';

@Component({
  selector: 'app-projet-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projet-dashboard.component.html',
  styleUrls: ['./projet-dashboard.component.scss'],
})
export class ProjetDashboardComponent implements OnInit {
  projets: Projet[] = [];
  similarProjects: Projet[] = [];
  stats: any;
  selectedProjectId: string = '';

  constructor(private projetService: ProjetsService) {}

  ngOnInit(): void {
    this.loadProjets();
    this.loadStats();
  }

  loadProjets(): void {
    this.projetService.getAll().subscribe({
      next: (data) => (this.projets = data),
      error: (err) => console.error('Erreur chargement projets:', err),
    });
  }

  loadStats(): void {
    this.projetService.getStats().subscribe({
      next: (data) => (this.stats = data),
      error: (err) => console.error('Erreur chargement stats:', err),
    });
  }

  showSimilar(): void {
    if (!this.selectedProjectId) {
      alert('Sélectionnez un projet pour voir les similaires.');
      return;
    }
    this.projetService.getSimilar(this.selectedProjectId).subscribe({
      next: (data) => (this.similarProjects = data),
      error: (err) => console.error('Erreur chargement similaires:', err),
    });
  }
}
