import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should toggle loading state', (done) => {
    service.loading$.subscribe((isLoading) => {
      if (isLoading) {
        expect(isLoading).toBe(true);
        done();
      }
    });

    service.show();
  });

  it('should hide loading state', (done) => {
    service.show();
    service.hide();
    service.loading$.subscribe((isLoading) => {
      expect(isLoading).toBe(false);
      done();
    });
  });
});
