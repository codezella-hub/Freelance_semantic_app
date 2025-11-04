import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployabilityStatsComponent } from './employability-stats.component';

describe('EmployabilityStatsComponent', () => {
  let component: EmployabilityStatsComponent;
  let fixture: ComponentFixture<EmployabilityStatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployabilityStatsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployabilityStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
