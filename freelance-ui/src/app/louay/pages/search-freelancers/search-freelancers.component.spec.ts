import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchFreelancersComponent } from './search-freelancers.component';

describe('SearchFreelancersComponent', () => {
  let component: SearchFreelancersComponent;
  let fixture: ComponentFixture<SearchFreelancersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchFreelancersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchFreelancersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
