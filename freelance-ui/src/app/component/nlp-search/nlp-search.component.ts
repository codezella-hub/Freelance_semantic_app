import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NlpQueryService, NLPQueryResponse, ExamplesResponse } from '../../services/nlp-query.service';

@Component({
  selector: 'app-nlp-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './nlp-search.component.html',
  styleUrls: ['./nlp-search.component.scss']
})
export class NlpSearchComponent implements OnInit {
  query: string = '';
  loading: boolean = false;
  results: any[] = [];
  resultType: string = '';
  resultCount: number = 0;
  sparqlQuery: string = '';
  error: string | null = null;
  showExamples: boolean = true;
  showSparql: boolean = false;
  
  eventExamples: string[] = [];
  certificationExamples: string[] = [];

  constructor(private nlpService: NlpQueryService) {}

  ngOnInit(): void {
    this.loadExamples();
  }

  loadExamples(): void {
    this.nlpService.getExamples().subscribe({
      next: (response: ExamplesResponse) => {
        this.eventExamples = response.eventExamples;
        this.certificationExamples = response.certificationExamples;
      },
      error: (err) => {
        console.error('Error loading examples:', err);
      }
    });
  }

  submitQuery(): void {
    if (!this.query.trim()) {
      this.error = 'Veuillez entrer une requête';
      return;
    }

    this.loading = true;
    this.error = null;
    this.results = [];
    this.showExamples = false;

    this.nlpService.processQuery(this.query).subscribe({
      next: (response: NLPQueryResponse) => {
        this.loading = false;
        
        if (response.success) {
          this.results = response.results || [];
          this.resultType = response.type || '';
          this.resultCount = response.count || 0;
          this.sparqlQuery = response.sparql || '';
        } else {
          this.error = response.error || 'Erreur inconnue';
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Erreur lors du traitement de la requête';
        console.error('Error:', err);
      }
    });
  }

  useExample(example: string): void {
    this.query = example;
    this.submitQuery();
  }

  clearResults(): void {
    this.query = '';
    this.results = [];
    this.resultType = '';
    this.resultCount = 0;
    this.sparqlQuery = '';
    this.error = null;
    this.showExamples = true;
    this.showSparql = false;
  }

  toggleSparql(): void {
    this.showSparql = !this.showSparql;
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
}

