import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployabilityListComponent } from './employability-list.component';

describe('EmployabilityListComponent', () => {
  let component: EmployabilityListComponent;
  let fixture: ComponentFixture<EmployabilityListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployabilityListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployabilityListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
