import { Injectable } from '@angular/core';
import { QueryEntity } from '@datorama/akita';
import { RestaurantStore, RestaurantState } from './restaurant.store';

@Injectable({ providedIn: 'root' })
export class RestaurantQuery extends QueryEntity<RestaurantState> {
  constructor(protected override store: RestaurantStore) {
    super(store);
  }

  get activeRestaurant$() {
  return this.selectActive();
}

get activeRestaurantId$() {
  return this.selectActiveId();
}
}
