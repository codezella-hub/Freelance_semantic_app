import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MissionService } from '../../services/mission.service';
import { Mission } from '../../models/mission';

@Component({
  selector: 'app-missions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.scss']
})
export class MissionsComponent {
  private fb = inject(FormBuilder);
  private service = inject(MissionService);

  missions: Mission[] = [];
  editingId: string | null = null;

  form = this.fb.group({
    id: this.fb.control<string | undefined>(undefined),
    titre: this.fb.control<string>('', { validators: [Validators.required] }),
    description: this.fb.control<string>(''),
    budget: this.fb.control<number | undefined>(undefined),
    statut: this.fb.control<string>('')
  });

  ngOnInit() { this.reload(); }

  reload() {
    this.service.getAll().subscribe(data => this.missions = data);
  }

  onSearch(query: string, status: string) {
    this.service.search({ q: query, status }).subscribe(d => this.missions = d);
  }

  startAdd() {
    this.editingId = null;
    this.form.reset({ id: undefined, titre: '', description: '', budget: undefined, statut: '' });
  }

  startEdit(m: Mission) {
    this.editingId = m.id;
    this.form.reset({
      id: m.id,
      titre: m.titre ?? '',
      description: m.description ?? '',
      budget: m.budget ?? undefined,
      statut: m.statut ?? ''
    });
  }

  submit() {
    const raw = this.form.getRawValue();
    const payload: Partial<Mission> = {
      titre: raw.titre ?? undefined,
      description: raw.description ?? undefined,
      budget: raw.budget ?? undefined,
      statut: raw.statut ?? undefined
    };
    if (this.editingId) {
      this.service.update(this.editingId, payload).subscribe(() => this.reload());
    } else {
      this.service.create(payload).subscribe(() => this.reload());
    }
    this.form.reset({ id: undefined, titre: '', description: '', budget: undefined, statut: '' });
  }

  remove(m: Mission) {
    this.service.delete(m.id).subscribe(() => this.reload());
  }
}


