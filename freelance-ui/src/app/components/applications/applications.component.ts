import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ApplicationService } from '../../services/application.service';
import { ApplicationDto } from '../../models/application';

@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './applications.component.html',
  styleUrls: ['./applications.component.scss']
})
export class ApplicationsComponent {
  private fb = inject(FormBuilder);
  private service = inject(ApplicationService);

  applications: ApplicationDto[] = [];
  editingId: string | null = null;

  form = this.fb.group({
    id: this.fb.control<string | undefined>(undefined),
    status: this.fb.control<string>('', { validators: [Validators.required] }),
    date: this.fb.control<string>(''),
    missionUri: this.fb.control<string>('', { validators: [Validators.required] }),
    applicantUri: this.fb.control<string>('', { validators: [Validators.required] })
  });

  ngOnInit() { this.reload(); }

  reload() { this.service.getAll().subscribe(data => this.applications = data); }

  onSearch(status: string, missionUri: string) {
    this.service.search({ status, missionUri }).subscribe(d => this.applications = d);
  }

  startAdd() {
    this.editingId = null;
    this.form.reset({ id: undefined, status: '', date: '', missionUri: '', applicantUri: '' });
  }

  startEdit(a: ApplicationDto) {
    if (!a.id) return;
    this.editingId = a.id;
    this.form.reset({
      id: a.id,
      status: a.status ?? '',
      date: a.date ?? '',
      missionUri: a.missionUri ?? '',
      applicantUri: a.applicantUri ?? ''
    });
  }

  submit() {
    const raw = this.form.getRawValue();
    const payload: Partial<ApplicationDto> = {
      status: raw.status ?? undefined,
      date: raw.date ?? undefined,
      missionUri: raw.missionUri ?? undefined,
      applicantUri: raw.applicantUri ?? undefined
    };
    if (this.editingId) {
      this.service.update(this.editingId, payload).subscribe(() => this.reload());
    } else {
      this.service.create(payload).subscribe(() => this.reload());
    }
    this.form.reset({ id: undefined, status: '', date: '', missionUri: '', applicantUri: '' });
  }

  remove(a: ApplicationDto) {
    if (!a.id) return;
    this.service.delete(a.id).subscribe(() => this.reload());
  }
}


