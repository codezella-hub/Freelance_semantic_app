import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployerPotentialListComponent } from './employer-potential-list.component';

describe('EmployerPotentialListComponent', () => {
  let component: EmployerPotentialListComponent;
  let fixture: ComponentFixture<EmployerPotentialListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployerPotentialListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployerPotentialListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
