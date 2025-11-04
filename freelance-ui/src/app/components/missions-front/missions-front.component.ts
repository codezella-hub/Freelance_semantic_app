import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MissionService } from '../../services/mission.service';
import { ApplicationService } from '../../services/application.service';
import { Mission } from '../../models/mission';
import { ApplicationDto } from '../../models/application';

@Component({
  selector: 'app-missions-front',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './missions-front.component.html',
  styleUrls: ['./missions-front.component.scss']
})
export class MissionsFrontComponent implements OnInit {
  missions: Mission[] = [];
  filteredMissions: Mission[] = [];
  loading: boolean = false;
  error: string | null = null;
  
  // Search and filters
  searchQuery: string = '';
  statusFilter: string = '';
  budgetMin: number | null = null;
  budgetMax: number | null = null;
  
  // Application
  applyingToMissionId: string | null = null;
  applicantUri: string = 'http://example.com/freelance#Freelancer-1'; // TODO: Get from auth

  constructor(
    private missionService: MissionService,
    private applicationService: ApplicationService
  ) {}

  ngOnInit(): void {
    this.loadMissions();
  }

  loadMissions(): void {
    this.loading = true;
    this.error = null;
    
    if (this.searchQuery || this.statusFilter) {
      this.missionService.search({
        q: this.searchQuery || undefined,
        status: this.statusFilter || undefined
      }).subscribe({
        next: (data) => {
          this.missions = data;
          this.applyFilters();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des missions';
          this.loading = false;
          console.error('Error:', err);
        }
      });
    } else {
      this.missionService.getAll().subscribe({
        next: (data) => {
          this.missions = data;
          this.applyFilters();
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des missions';
          this.loading = false;
          console.error('Error:', err);
        }
      });
    }
  }

  applyFilters(): void {
    this.filteredMissions = [...this.missions];
    
    // Budget filters
    if (this.budgetMin !== null) {
      this.filteredMissions = this.filteredMissions.filter(m => 
        m.budget !== null && m.budget >= this.budgetMin!
      );
    }
    if (this.budgetMax !== null) {
      this.filteredMissions = this.filteredMissions.filter(m => 
        m.budget !== null && m.budget <= this.budgetMax!
      );
    }
  }

  onSearch(): void {
    this.loadMissions();
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.statusFilter = '';
    this.budgetMin = null;
    this.budgetMax = null;
    this.loadMissions();
  }

  applyToMission(mission: Mission): void {
    if (!mission.id) {
      alert('Erreur : Mission invalide');
      return;
    }
    
    this.applyingToMissionId = mission.id;
    const application: Partial<ApplicationDto> = {
      status: 'PENDING',
      date: new Date().toISOString().split('T')[0], // Format YYYY-MM-DD
      missionUri: mission.id,
      applicantUri: this.applicantUri
    };

    this.applicationService.create(application).subscribe({
      next: (createdApp) => {
        console.log('Application créée avec succès:', createdApp);
        alert('Candidature envoyée avec succès !\nVous pouvez la vérifier dans la section admin.');
        this.applyingToMissionId = null;
      },
      error: (err) => {
        console.error('Erreur lors de l\'envoi de la candidature:', err);
        alert('Erreur lors de l\'envoi de la candidature. Veuillez réessayer.');
        this.applyingToMissionId = null;
      }
    });
  }

  formatBudget(budget: number | null): string {
    if (budget === null) return 'Non spécifié';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(budget);
  }

  getStatusBadgeClass(status: string | null): string {
    if (!status) return '';
    switch (status.toUpperCase()) {
      case 'OPEN':
        return 'status-open';
      case 'IN_PROGRESS':
        return 'status-progress';
      case 'CLOSED':
        return 'status-closed';
      default:
        return '';
    }
  }
}

