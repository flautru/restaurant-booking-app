import { Injectable } from '@angular/core';
import { RestaurantStore } from './restaurant.store';
import { Restaurant } from '../model/restaurant.model';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  constructor(
    private restaurantStore: RestaurantStore,
    private http: HttpClient
  ) {}

  loadRestaurants() {
  // Pas de return Observable, juste side effect
  this.restaurantStore.setLoading(true);

  this.http.get<Restaurant[]>('http://localhost:8080/api/restaurants').pipe(
    tap(restaurants => this.restaurantStore.set(restaurants)),
    catchError(error => {
      this.restaurantStore.setError(error);
      return EMPTY;
    }),
    finalize(() => this.restaurantStore.setLoading(false))
  ).subscribe(); // Subscribe dans le service, pas le composant
}

  setActiveRestaurant(id: string) {
    this.restaurantStore.setActive(id);
  }

  resetStore() {
    this.restaurantStore.reset();
  }
}
