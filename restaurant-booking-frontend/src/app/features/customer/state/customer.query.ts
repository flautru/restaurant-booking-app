import { Injectable } from '@angular/core';
import { QueryEntity } from '@datorama/akita';
import { CustomerStore, CustomerState } from './customer.store';

@Injectable({ providedIn: 'root' })
export class CustomerQuery extends QueryEntity<CustomerState> {
  constructor(protected override store: CustomerStore) {
    super(store);
  }

  get activeCustomer$() {
  return this.selectActive();
}

get activeCustomerId$() {
  return this.selectActiveId();
}
}
