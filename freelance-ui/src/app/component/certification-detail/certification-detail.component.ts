import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CertificationService, Certification } from '../../services/certification.service';
import { jsPDF } from 'jspdf';

@Component({
  selector: 'app-certification-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './certification-detail.component.html',
  styleUrls: ['./certification-detail.component.scss']
})
export class CertificationDetailComponent implements OnInit {
  certification: Certification | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private certificationService: CertificationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const uri = this.route.snapshot.paramMap.get('uri');
    if (uri) {
      this.loadCertification(decodeURIComponent(uri));
    }
  }

  loadCertification(uri: string): void {
    this.loading = true;
    this.error = null;

    this.certificationService.getCertificationByUri(uri).subscribe({
      next: (response) => {
        if (response.certification) {
          this.certification = response.certification;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading certification:', err);
        this.error = 'Erreur lors du chargement de la certification';
        this.loading = false;
      }
    });
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

  getCertificationTypeLabel(type: string): string {
    return type === 'FormalCertification' ? 'Formelle' : 'Informelle';
  }

  isExpired(expirationDate: string): boolean {
    if (!expirationDate) return false;
    return new Date(expirationDate) < new Date();
  }

  goBack(): void {
    this.router.navigate(['/certifications']);
  }

  editCertification(): void {
    if (this.certification?.uri) {
      this.router.navigate(['/certifications/edit', this.certification.uri]);
    }
  }

  deleteCertification(): void {
    if (this.certification?.uri && confirm('Êtes-vous sûr de vouloir supprimer cette certification ?')) {
      this.certificationService.deleteCertification(this.certification.uri).subscribe({
        next: () => {
          this.router.navigate(['/certifications']);
        },
        error: (err) => {
          console.error('Error deleting certification:', err);
          this.error = 'Erreur lors de la suppression de la certification';
        }
      });
    }
  }

  downloadPDF(): void {
    if (!this.certification) return;

    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    const margin = 20;
    let yPosition = 20;

    // Titre du document
    doc.setFontSize(24);
    doc.setTextColor(17, 153, 142);
    doc.text('CERTIFICATION', pageWidth / 2, yPosition, { align: 'center' });
    yPosition += 15;

    // Ligne de séparation
    doc.setDrawColor(17, 153, 142);
    doc.setLineWidth(0.5);
    doc.line(margin, yPosition, pageWidth - margin, yPosition);
    yPosition += 15;

    // Nom de la certification
    doc.setFontSize(18);
    doc.setTextColor(0, 0, 0);
    doc.text(this.certification.certificationName || 'Sans nom', margin, yPosition);
    yPosition += 12;

    // Type de certification
    doc.setFontSize(12);
    doc.setTextColor(100, 100, 100);
    doc.text(`Type: ${this.getCertificationTypeLabel(this.certification.certificationType)}`, margin, yPosition);
    yPosition += 10;

    // Statut (Valide/Expirée)
    const expired = this.isExpired(this.certification.expirationDate);
    doc.setTextColor(expired ? 220 : 40, expired ? 53 : 167, expired ? 69 : 69);
    doc.text(`Statut: ${expired ? 'Expirée' : 'Valide'}`, margin, yPosition);
    yPosition += 15;

    // Section Émetteur
    doc.setFontSize(14);
    doc.setTextColor(17, 153, 142);
    doc.text('Émetteur', margin, yPosition);
    yPosition += 8;
    doc.setFontSize(12);
    doc.setTextColor(0, 0, 0);
    doc.text(this.certification.issuedBy || 'Non spécifié', margin + 5, yPosition);
    yPosition += 15;

    // Section Dates
    doc.setFontSize(14);
    doc.setTextColor(17, 153, 142);
    doc.text('Dates importantes', margin, yPosition);
    yPosition += 8;
    doc.setFontSize(12);
    doc.setTextColor(0, 0, 0);
    doc.text(`Date d'émission: ${this.formatDate(this.certification.issueDate)}`, margin + 5, yPosition);
    yPosition += 8;
    doc.text(`Date d'expiration: ${this.formatDate(this.certification.expirationDate)}`, margin + 5, yPosition);
    yPosition += 15;

    // Section Détails techniques
    doc.setFontSize(14);
    doc.setTextColor(17, 153, 142);
    doc.text('Détails techniques', margin, yPosition);
    yPosition += 8;
    doc.setFontSize(10);
    doc.setTextColor(100, 100, 100);

    // URI (avec retour à la ligne si nécessaire)
    const uri = this.certification.uri || 'N/A';
    const splitUri = doc.splitTextToSize(`URI: ${uri}`, pageWidth - 2 * margin);
    doc.text(splitUri, margin + 5, yPosition);
    yPosition += splitUri.length * 5 + 10;

    // Pied de page
    const footerY = doc.internal.pageSize.getHeight() - 20;
    doc.setFontSize(10);
    doc.setTextColor(150, 150, 150);
    doc.text(`Généré le ${new Date().toLocaleDateString('fr-FR')}`, pageWidth / 2, footerY, { align: 'center' });

    // Télécharger le PDF
    const fileName = `certification_${this.certification.certificationName?.replace(/[^a-z0-9]/gi, '_').toLowerCase() || 'document'}.pdf`;
    doc.save(fileName);
  }
}

