import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { RdfApiService } from '../services/rdf-api.service';
import { AIService } from '../services/ai.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject, takeUntil, Observable, finalize } from 'rxjs';

interface Contract {
  id?: number;
  title: string;
  description: string;
  skills: string;
  duration: number;
  amount: number;
  startDate: string;
}

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
  ],
  providers: [AIService],
  templateUrl: './contracts.component.html',
  styleUrls: ['./contracts.component.scss']
})
export class ContractsComponent implements OnInit, OnDestroy {
  contracts: Contract[] = [];
  contractForm!: FormGroup;
  aiRecommendation: string = '';
  loading: boolean = false;
  private destroy$ = new Subject<void>();

  constructor(
    private rdfService: RdfApiService,
    private router: Router,
    private aiService: AIService,
    private fb: FormBuilder
  ) {
    this.initForm();
  }

  private initForm(): void {
    this.contractForm = this.fb.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      skills: ['', [Validators.required]],
      duration: ['', [Validators.required, Validators.min(1)]],
      budget: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadContracts();
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '';
    return date.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  extractContractId(uri?: string): string {
    if (!uri) return '';
    const parts = uri.split('#');
    return parts[parts.length - 1] || uri;
  }

  delete(id: number): void {
    if (!confirm('Delete this contract?')) return;
    
    this.rdfService.deleteContract(id)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loadContracts())
      )
      .subscribe({
        error: (error: unknown) => {
          console.error('Delete contract failed', error);
          alert('Failed to delete contract. Please try again.');
        }
      });
  }

  getPriceRecommendation(): void {
    const formValues = this.contractForm.getRawValue();
    const { description, skills, duration } = formValues;

    if (!description || !skills || !duration) {
      alert('Please fill in description, skills, and duration first');
      return;
    }

    this.loading = true;
    this.aiService.analyzePricing(description, skills, Number(duration))
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (recommendation: string) => {
          this.aiRecommendation = recommendation;
        },
        error: (error: unknown) => {
          console.error('AI analysis failed', error);
          alert('Failed to get AI recommendation. Please try again.');
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  improveDescription(): void {
    const description = this.contractForm.get('description')?.value;
    if (!description) {
      alert('Please enter a description first');
      return;
    }

    this.loading = true;
    this.aiService.improveDescription(description)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (improvedDescription: string) => {
          this.contractForm.patchValue({ description: improvedDescription });
        },
        error: (error: unknown) => {
          console.error('Description improvement failed', error);
          alert('Failed to improve description. Please try again.');
        }
      });
  }

  onSubmit(): void {
    if (this.contractForm.invalid) {
      alert('Please fill in all required fields correctly');
      return;
    }

    const formValues = this.contractForm.getRawValue();
    const contract: Contract = {
      title: formValues.title,
      description: formValues.description,
      skills: formValues.skills,
      duration: Number(formValues.duration),
      amount: Number(formValues.budget),
      startDate: new Date().toISOString()
    };

    this.loading = true;
    this.rdfService.createContract(contract)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: () => {
          this.contractForm.reset();
          this.aiRecommendation = '';
          this.loadContracts();
          alert('Contract created successfully!');
        },
        error: (error: unknown) => {
          console.error('Failed to create contract', error);
          alert('Failed to create contract. Please try again.');
        }
      });
  }

  private loadContracts(): void {
    this.rdfService.getJpaContracts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: Contract[]) => {
          this.contracts = data;
        },
        error: (error: unknown) => {
          console.error('Failed to load contracts', error);
        }
      });
  }
}