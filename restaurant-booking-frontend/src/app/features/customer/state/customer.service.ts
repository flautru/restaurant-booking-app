import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { tap, catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { CustomerStore } from './customer.store';
import { CustomerQuery } from './customer.query';
import { Customer } from '../model/customer.model';


@Injectable({ providedIn: 'root' })
export class CustomerService {
  constructor(
    private customerStore: CustomerStore,
    private http: HttpClient,
    private customerQuery: CustomerQuery
  ) {}

  BASE_URL: string = 'http://localhost:8080';

  loadCustomers() {
    // Pas de return Observable, juste side effect
    this.customerStore.setLoading(true);

    this.http
      .get<Customer[]>(this.BASE_URL + '/api/customers')
      .pipe(
        tap((customers) => this.customerStore.set(customers)),
        catchError((error) => {
          this.customerStore.setError(error);
          return EMPTY;
        }),
        finalize(() => this.customerStore.setLoading(false))
      )
      .subscribe(); // Subscribe dans le service, pas le composant
  }

  setActiveCustomerId(customerId: number): void {
    console.log('🎯 Setting active customer:', customerId);
    this.customerStore.setActive(customerId);
  }

  getActiveCustomer(): Customer | undefined {
    const activeId = this.customerQuery.getActiveId();
    return activeId ? this.customerQuery.getEntity(activeId) : undefined;
  }

  clearActiveCustomer(): void {
    this.customerStore.setActive(null);
  }

  resetStore() {
    this.customerStore.reset();
  }
}
