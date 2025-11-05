import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFreelancersComponent } from './add-freelancers.component';

describe('AddFreelancersComponent', () => {
  let component: AddFreelancersComponent;
  let fixture: ComponentFixture<AddFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
