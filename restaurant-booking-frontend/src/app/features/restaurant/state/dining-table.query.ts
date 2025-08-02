import { Injectable } from '@angular/core';
import { QueryEntity } from '@datorama/akita';
import { DiningTableState, DiningTableStore } from './dining-table.store';

@Injectable({ providedIn: 'root' })
export class DiningTableQuery extends QueryEntity<DiningTableState> {
  constructor(protected override store: DiningTableStore) {
    super(store);
  }

  get activeDiningTable$() {
  return this.selectActive();
}

get activeDiningTableId$() {
  return this.selectActiveId();
}
}
