// restaurant-detail.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Restaurant } from '../../model/restaurant.model';
import { RestaurantQuery } from '../../state/restaurant.query';
import { RestaurantService } from '../../state/restaurant.service';
import { DiningTableQuery } from '../../state/dining-table.query';
import { DiningTableService } from '../../state/dining-table.service';

@Component({
  selector: 'app-restaurant-detail',
  templateUrl: './restaurant-detail.component.html',
  styleUrls: ['./restaurant-detail.component.scss'],
  standalone: false
})
export class RestaurantDetailComponent implements OnInit, OnDestroy {
  restaurant$: Observable<Restaurant | undefined>;
  loading$: Observable<boolean>;
  error$: Observable<any>;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantQuery: RestaurantQuery,
    private restaurantService: RestaurantService,
    private diningTableService: DiningTableService,
    private diningTableQuery: DiningTableQuery
  ) {
    this.loading$ = this.restaurantQuery.selectLoading();
    this.error$ = this.restaurantQuery.selectError();
    this.restaurant$ = this.restaurantQuery.selectActive() as Observable<Restaurant | undefined>;
  }

  ngOnInit(): void {
    const restaurantId = +this.route.snapshot.params['id'];
    console.log('🏗️ RestaurantDetail - Loading restaurant:', restaurantId);

    this.restaurantService.setActiveRestaurant(restaurantId);
    this.diningTableService.loadDiningTableForRestaurant(restaurantId);
  }

  ngOnDestroy(): void {
    this.restaurantService.clearActiveRestaurant();
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Navigation Actions
  goBack(): void {
    this.router.navigate(['/restaurants']);
  }

  editRestaurant(): void {
    const restaurant = this.restaurantQuery.getActiveId();
    console.log('✏️ Edit restaurant:', restaurant);
    // TODO: Navigation vers formulaire d'édition
  }

  viewTables(): void {
    const restaurantId = this.restaurantQuery.getActiveId();
    console.log('🪑 View tables for restaurant:', restaurantId);
    // TODO: Navigation vers /restaurants/:id/tables
  }

  viewBookings(): void {
    const restaurantId = this.restaurantQuery.getActiveId();
    console.log('📅 View bookings for restaurant:', restaurantId);
    // TODO: Navigation vers /restaurants/:id/bookings
  }

  viewReports(): void {
    console.log('📊 View reports');
    // TODO: Navigation vers rapports
  }

  // Placeholder methods pour les stats
  getTableCount(): number {
    return this.diningTableQuery.getCount(); 
  }

  getBookingCount(): number {
    return 45; // TODO: Vraie logique
  }
}
