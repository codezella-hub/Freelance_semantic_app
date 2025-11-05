import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { FreelancerService } from '../../services/freelancer.service';
import { Freelancer, Skill } from '../../services/freelancer.model';

@Component({
  selector: 'app-update-freelancers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './update-freelancers.component.html',
  styleUrl: './update-freelancers.component.scss'
})
export class UpdateFreelancersComponent implements OnInit {
  private freelancerService = inject(FreelancerService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  freelancerForm: FormGroup;
  loading = false;
  submitted = false;
  freelancerId: string = '';
  currentFreelancer: Freelancer | null = null;

  experienceLevels = [
    { value: 'Junior', label: '👶 Junior' },
    { value: 'Intermediaire', label: '🚀 Intermédiaire' },
    { value: 'Senior', label: '🎯 Senior' },
    { value: 'Expert', label: '🏆 Expert' }
  ];

  skillLevels = [
    { value: 'Débutant', label: '⭐ Débutant' },
    { value: 'Intermediaire', label: '⭐⭐ Intermédiaire' },
    { value: 'Avancé', label: '⭐⭐⭐ Avancé' },
    { value: 'Expert', label: '⭐⭐⭐⭐ Expert' }
  ];

  constructor() {
    this.freelancerForm = this.createForm();
  }

  ngOnInit(): void {
    this.freelancerId = this.route.snapshot.paramMap.get('id') || '';

    if (this.freelancerId) {
      this.loadFreelancerData();
    }
  }

  loadFreelancerData(): void {
    this.loading = true;
    this.freelancerService.getFreelancerById(this.freelancerId).subscribe({
      next: (freelancer) => {
        this.currentFreelancer = freelancer;
        this.populateForm(freelancer);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading freelancer:', error);
        this.loading = false;
        alert('Erreur lors du chargement du freelancer.');
      }
    });
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      experienceLevel: ['', Validators.required],
      skills: this.fb.array([])
    });
  }

  populateForm(freelancer: Freelancer): void {
    this.freelancerForm.patchValue({
      name: freelancer.name,
      experienceLevel: freelancer.experienceLevel
    });

    // Clear existing skills
    while (this.skills.length !== 0) {
      this.skills.removeAt(0);
    }

    // Add skills from freelancer
    freelancer.skills.forEach(skill => {
      this.addSkill(skill);
    });

    // If no skills, add one empty skill
    if (freelancer.skills.length === 0) {
      this.addSkill();
    }
  }

  get skills(): FormArray {
    return this.freelancerForm.get('skills') as FormArray;
  }

  createSkillGroup(skill: Skill = { id: '', name: '', level: 'Intermediaire' }): FormGroup {
    return this.fb.group({
      name: [skill.name, [Validators.required, Validators.minLength(2)]],
      level: [skill.level, Validators.required]
    });
  }

  addSkill(skill?: Skill): void {
    this.skills.push(this.createSkillGroup(skill));
  }

  removeSkill(index: number): void {
    if (this.skills.length > 1) {
      this.skills.removeAt(index);
    } else {
      // Si c'est la dernière compétence, on la réinitialise
      this.skills.at(index).reset({ name: '', level: 'Intermediaire' });
    }
  }

  moveSkillUp(index: number): void {
    if (index > 0) {
      const skill = this.skills.at(index);
      this.skills.removeAt(index);
      this.skills.insert(index - 1, skill);
    }
  }

  moveSkillDown(index: number): void {
    if (index < this.skills.length - 1) {
      const skill = this.skills.at(index);
      this.skills.removeAt(index);
      this.skills.insert(index + 1, skill);
    }
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.freelancerForm.valid && this.currentFreelancer) {
      this.loading = true;

      const formValue = this.freelancerForm.value;

      // Filtrer les compétences vides
      const validSkills = formValue.skills.filter((skill: Skill) =>
        skill.name && skill.name.trim().length > 0
      );

      const updatedFreelancer: Freelancer = {
        id: this.currentFreelancer.id,
        name: formValue.name.trim(),
        experienceLevel: formValue.experienceLevel,
        skills: validSkills
      };

      this.freelancerService.updateFreelancer(this.freelancerId, updatedFreelancer).subscribe({
        next: () => {
          this.loading = false;
          this.submitted = false;

          // Afficher un message de succès
          alert('Freelancer modifié avec succès !');

          // Rediriger vers la liste
          this.router.navigate(['/freelance/list']);
        },
        error: (error) => {
          console.error('Error updating freelancer:', error);
          this.loading = false;
          alert('Erreur lors de la modification du freelancer. Veuillez réessayer.');
        }
      });
    } else {
      // Marquer tous les champs comme touchés pour afficher les erreurs
      this.markFormGroupTouched(this.freelancerForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup | FormArray): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control instanceof FormGroup || control instanceof FormArray) {
        this.markFormGroupTouched(control);
      } else {
        control?.markAsTouched();
      }
    });
  }

  getFieldError(fieldName: string, index?: number): string {
    let control: any;

    if (index !== undefined) {
      const skillGroup = this.skills.at(index) as FormGroup;
      control = skillGroup.get(fieldName);
    } else {
      control = this.freelancerForm.get(fieldName);
    }

    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return 'Ce champ est obligatoire';
      }
      if (control.errors['minlength']) {
        return `Minimum ${control.errors['minlength'].requiredLength} caractères`;
      }
      if (control.errors['maxlength']) {
        return `Maximum ${control.errors['maxlength'].requiredLength} caractères`;
      }
    }

    return '';
  }

  isSubmitDisabled(): boolean {
    return this.freelancerForm.invalid ||
      this.loading ||
      this.skills.length === 0 ||
      this.skills.controls.every(skill => !skill.get('name')?.value.trim());
  }

  hasNoValidSkills(): boolean {
    return this.skills.length > 0 &&
      this.skills.controls.every(skill => {
        const nameControl = skill.get('name');
        return !nameControl || !nameControl.value || !nameControl.value.trim();
      });
  }
}
