import { TestBed, inject } from '@angular/core/testing';

import { SseService } from './sse-service.service';

describe('SseService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SseService]
    });
  });

  it('should be created', inject([SseService], (service: SseService) => {
    expect(service).toBeTruthy();
  }));
});
