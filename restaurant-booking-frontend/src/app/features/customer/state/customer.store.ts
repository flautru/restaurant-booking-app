import { Injectable } from '@angular/core';
import { EntityState, EntityStore, StoreConfig } from '@datorama/akita';
import { Customer } from '../model/customer.model';

export interface CustomerState extends EntityState<Customer, number> {}

@Injectable({ providedIn: 'root' })
@StoreConfig({ name: 'customer' })
export class CustomerStore extends EntityStore<CustomerState, Customer> {
  constructor() {
    super();
  }
}
