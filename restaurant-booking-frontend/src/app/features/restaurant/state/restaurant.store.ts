import { Injectable } from '@angular/core';
import { EntityState, EntityStore, StoreConfig } from '@datorama/akita';
import { Restaurant } from '../model/restaurant.model';

export interface RestaurantState extends EntityState<Restaurant, number> {}

@Injectable({ providedIn: 'root' })
@StoreConfig({ name: 'restaurant' })
export class RestaurantStore extends EntityStore<RestaurantState, Restaurant> {
  constructor() {
    super(); 
  }
}
