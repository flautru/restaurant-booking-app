// customer-list.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest, Subject } from 'rxjs';
import {
  map,
  debounceTime,
  distinctUntilChanged,
  startWith,
  takeUntil,
} from 'rxjs/operators';
import { Customer } from '../../model/customer.model';
import { CustomerQuery } from '../../state/customer.query';
import { CustomerService } from '../../state/customer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss'],
  standalone: false,
})
export class CustomerListComponent implements OnInit, OnDestroy {
  // ===== REACTIVE STREAMS =====
  customers$: Observable<Customer[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;

  // Search multi-critères
  private searchTerm$ = new BehaviorSubject<string>('');

  // Filtrage intelligent
  filteredCustomers$: Observable<Customer[]>;

  private destroy$ = new Subject<void>();

  constructor(
    private customerQuery: CustomerQuery,
    private customerService: CustomerService,
    private router: Router
  ) {
    this.customers$ = this.customerQuery.selectAll();
    this.loading$ = this.customerQuery.selectLoading();
    this.error$ = this.customerQuery.selectError();

    // ✨ MAGIC FILTERING - Multi-champs
    this.filteredCustomers$ = combineLatest([
      this.customers$,
      this.searchTerm$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        startWith('')
      ),
    ]).pipe(
      map(([customers, searchTerm]) => {
        if (!searchTerm.trim()) {
          return customers;
        }

        const term = searchTerm.toLowerCase().trim();
        return customers.filter(
          (customer) =>
            customer.name.toLowerCase().includes(term) ||
            (customer.email && customer.email.toLowerCase().includes(term)) ||
            (customer.phoneNumber && customer.phoneNumber.includes(term))
        );
      }),
      takeUntil(this.destroy$)
    );
  }

  ngOnInit(): void {
    console.log('👥 CustomerList - Loading customers');
    this.customerService.loadCustomers();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ===== USER ACTIONS =====
  onSearch(searchTerm: string): void {
    this.searchTerm$.next(searchTerm);
  }

  selectCustomer(customer: Customer): void {
    console.log('👤 Customer selected:', customer.name);
    this.router.navigate(['/customers', customer.id]);
  }

  addCustomer(): void {
    console.log('➕ Add new customer');
    // TODO: Navigation vers formulaire
  }

  trackByCustomerId(index: number, customer: Customer): number {
    return customer.id;
  }

  get hasCustomers$() {
  return this.customers$.pipe(
    map(customers => customers && customers.length > 0)
  );
}

get hasNoCustomers$() {
  return this.customers$.pipe(
    map(customers => customers && customers.length === 0)
  );
}
}
