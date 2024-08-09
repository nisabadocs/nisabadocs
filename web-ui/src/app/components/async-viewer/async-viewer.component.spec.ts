import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsyncViewerComponent } from './async-viewer.component';

describe('AsyncViewerComponent', () => {
  let component: AsyncViewerComponent;
  let fixture: ComponentFixture<AsyncViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AsyncViewerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AsyncViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
