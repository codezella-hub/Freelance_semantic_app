import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import {FreelancerService} from '../../services/freelancer.service';
import {Freelancer, Skill} from '../../services/freelancer.model';


@Component({
  selector: 'app-add-freelancers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './add-freelancers.component.html',
  styleUrl: './add-freelancers.component.scss'
})
export class AddFreelancersComponent {
  private freelancerService = inject(FreelancerService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  freelancerForm: FormGroup;
  loading = false;
  submitted = false;

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
    // Ajouter une compétence vide par défaut
    this.addSkill();
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      experienceLevel: ['', Validators.required],
      skills: this.fb.array([])
    });
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

    if (this.freelancerForm.valid) {
      this.loading = true;

      const formValue = this.freelancerForm.value;

      // Filtrer les compétences vides
      const validSkills = formValue.skills.filter((skill: Skill) =>
        skill.name && skill.name.trim().length > 0
      );

      const freelancer: Freelancer = {
        id: '', // L'ID sera généré par le backend
        name: formValue.name.trim(),
        experienceLevel: formValue.experienceLevel,
        skills: validSkills
      };

      this.freelancerService.addFreelancer(freelancer).subscribe({
        next: (response) => {
          this.loading = false;
          this.submitted = false;

          // Afficher un message de succès
          alert('Freelancer ajouté avec succès !');

          // Rediriger vers la liste
          this.router.navigate(['/freelance/list']);
        },
        error: (error) => {
          console.error('Error adding freelancer:', error);
          this.loading = false;
          alert('Erreur lors de l\'ajout du freelancer. Veuillez réessayer.');
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

  // Méthode utilitaire pour désactiver le bouton de soumission
  isSubmitDisabled(): boolean {
    return this.freelancerForm.invalid ||
      this.loading ||
      this.skills.length === 0 ||
      this.skills.controls.every(skill => !skill.get('name')?.value.trim());
  }
  // Ajoutez cette méthode dans la classe AddFreelancersComponent
  hasNoValidSkills(): boolean {
    return this.skills.length > 0 &&
      this.skills.controls.every(skill => {
        const nameControl = skill.get('name');
        return !nameControl || !nameControl.value || !nameControl.value.trim();
      });
  }
}
