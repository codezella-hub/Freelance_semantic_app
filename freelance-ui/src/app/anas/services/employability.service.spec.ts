import { TestBed } from '@angular/core/testing';

import { EmployabilityService } from './employability.service';

describe('EmployabilityService', () => {
  let service: EmployabilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployabilityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
