import { Injectable } from '@angular/core';
import { EntityState, EntityStore, StoreConfig } from '@datorama/akita';
import { DiningTable } from '../model/dining-table.model';

export interface DiningTableState extends EntityState<DiningTable, number> {}

@Injectable({ providedIn: 'root' })
@StoreConfig({ name: 'diningTable' })
export class DiningTableStore extends EntityStore<DiningTableState, DiningTable> {
  constructor() {
    super();
  }
}
