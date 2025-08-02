import { Injectable } from '@angular/core';
import { RestaurantStore } from './restaurant.store';
import { Restaurant } from '../model/restaurant.model';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { RestaurantQuery } from './restaurant.query';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  constructor(
    private restaurantStore: RestaurantStore,
    private http: HttpClient,
    private restaurantQuery: RestaurantQuery
  ) {}

  BASE_URL: string = 'http://localhost:8080';

  loadRestaurants() {
    // Pas de return Observable, juste side effect
    this.restaurantStore.setLoading(true);

    this.http
      .get<Restaurant[]>(this.BASE_URL + '/api/restaurants')
      .pipe(
        tap((restaurants) => this.restaurantStore.set(restaurants)),
        catchError((error) => {
          this.restaurantStore.setError(error);
          return EMPTY;
        }),
        finalize(() => this.restaurantStore.setLoading(false))
      )
      .subscribe(); // Subscribe dans le service, pas le composant
  }

  setActiveRestaurant(restaurantId: number): void {
    console.log('🎯 Setting active restaurant:', restaurantId);
    this.restaurantStore.setActive(restaurantId);
  }

  getActiveRestaurant(): Restaurant | undefined {
    const activeId = this.restaurantQuery.getActiveId();
    return activeId ? this.restaurantQuery.getEntity(activeId) : undefined;
  }

  clearActiveRestaurant(): void {
    this.restaurantStore.setActive(null);
  }

  resetStore() {
    this.restaurantStore.reset();
  }
}
