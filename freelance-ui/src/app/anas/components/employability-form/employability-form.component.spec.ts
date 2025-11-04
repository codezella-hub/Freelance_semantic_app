import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployabilityFormComponent } from './employability-form.component';

describe('EmployabilityFormComponent', () => {
  let component: EmployabilityFormComponent;
  let fixture: ComponentFixture<EmployabilityFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployabilityFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployabilityFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
