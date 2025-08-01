import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantItem } from './restaurant-item.component';

describe('RestaurantItem', () => {
  let component: RestaurantItem;
  let fixture: ComponentFixture<RestaurantItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantItem],
    }).compileComponents();

    fixture = TestBed.createComponent(RestaurantItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
