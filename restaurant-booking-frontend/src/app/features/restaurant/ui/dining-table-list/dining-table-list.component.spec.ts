import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiningTableList } from './dining-table-list.component';

describe('DiningTableList', () => {
  let component: DiningTableList;
  let fixture: ComponentFixture<DiningTableList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiningTableList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiningTableList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
