import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CertificationService, Certification } from '../../services/certification.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-certification-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './certification-list.component.html',
  styleUrls: ['./certification-list.component.scss']
})
export class CertificationListComponent implements OnInit {
  certifications: Certification[] = [];
  filteredCertifications: Certification[] = [];
  loading = false;
  error: string | null = null;
  searchTerm = '';

  constructor(private certificationService: CertificationService) {}

  ngOnInit(): void {
    this.loadCertifications();
  }

  loadCertifications(): void {
    this.loading = true;
    this.error = null;
    
    this.certificationService.getAllCertifications().subscribe({
      next: (response) => {
        this.certifications = response.certifications || [];
        this.filteredCertifications = this.certifications;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des certifications';
        this.loading = false;
        console.error('Error loading certifications:', err);
      }
    });
  }

  searchCertifications(): void {
    if (this.searchTerm.trim()) {
      this.loading = true;
      this.certificationService.searchCertifications(this.searchTerm).subscribe({
        next: (response) => {
          this.filteredCertifications = response.certifications || [];
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors de la recherche';
          this.loading = false;
          console.error('Error searching certifications:', err);
        }
      });
    } else {
      this.filteredCertifications = this.certifications;
    }
  }

  deleteCertification(uri: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette certification ?')) {
      this.certificationService.deleteCertification(uri).subscribe({
        next: () => {
          this.loadCertifications();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
          console.error('Error deleting certification:', err);
        }
      });
    }
  }

  getCertificationTypeClass(type: string): string {
    return type === 'FormalCertification' ? 'badge bg-success' : 'badge bg-secondary';
  }

  getCertificationTypeLabel(type: string): string {
    return type === 'FormalCertification' ? 'Formelle' : 'Informelle';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  isExpired(expirationDate: string): boolean {
    if (!expirationDate) return false;
    return new Date(expirationDate) < new Date();
  }
}

