import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { DiningTableStore } from './dining-table.store';
import { DiningTableQuery } from './dining-table.query';
import { DiningTable } from '../model/dining-table.model';

@Injectable({ providedIn: 'root' })
export class DiningTableService {
  constructor(
    private diningTableStore: DiningTableStore,
    private http: HttpClient,
    private diningTableQuery: DiningTableQuery
  ) {}

  BASE_URL: string = 'http://localhost:8080';

  loadDiningTableForRestaurant(restaurantId: number): void {
    // Pas de return Observable, juste side effect
    this.diningTableStore.setLoading(true);

    this.http
      .get<DiningTable[]>(`${this.BASE_URL}/api/tables/restaurant/${restaurantId}`)
      .pipe(
        tap((diningTable) => this.diningTableStore.set(diningTable)),
        catchError((error) => {
          this.diningTableStore.setError(error);
          return EMPTY;
        }),
        finalize(() => this.diningTableStore.setLoading(false))
      )
      .subscribe(); // Subscribe dans le service, pas le composant
  }

  setActiveDiningTable(diningTableId: number): void {
    console.log('🎯 Setting active diningTable:', diningTableId);
    this.diningTableStore.setActive(diningTableId);
  }

  getActiveDiningTable(): DiningTable | undefined {
    const activeId = this.diningTableQuery.getActiveId();
    return activeId ? this.diningTableQuery.getEntity(activeId) : undefined;
  }

  clearActiveDiningTable(): void {
    this.diningTableStore.setActive(null);
  }

  resetStore() {
    this.diningTableStore.reset();
  }
}
