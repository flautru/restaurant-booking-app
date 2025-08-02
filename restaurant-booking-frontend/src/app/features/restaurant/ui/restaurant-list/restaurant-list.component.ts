import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, startWith, takeUntil } from 'rxjs/operators';
import { Restaurant } from '../../model/restaurant.model';
import { RestaurantQuery } from '../../state/restaurant.query';
import { RestaurantService } from '../../state/restaurant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.scss'],
  standalone: false
})
export class RestaurantListComponent implements OnInit, OnDestroy {
  // ===== REACTIVE STREAMS =====

  // Source streams (les "sources de vérité")
  restaurants$: Observable<Restaurant[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;

  // Search stream - le cœur du pattern réactif !
  private searchTerm$ = new BehaviorSubject<string>('');

  // Stream dérivé - automatiquement synchronisé !
  filteredRestaurants$: Observable<Restaurant[]>;

  // Cleanup stream
  private destroy$ = new Subject<void>();

  constructor(
    private restaurantQuery: RestaurantQuery,
    private restaurantService: RestaurantService,
    private router: Router
  ) {
    // Initialisation des streams après l'assignation de restaurantQuery
    this.restaurants$ = this.restaurantQuery.selectAll();
    this.loading$ = this.restaurantQuery.selectLoading();
    this.error$ = this.restaurantQuery.selectError();

    this.filteredRestaurants$ = combineLatest([
      this.restaurants$,
      this.searchTerm$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        startWith('')             // Commence avec une recherche vide
      )
    ]).pipe(
      map(([restaurants, searchTerm]) => {
        console.log('🔍 Filtering:', { restaurantsCount: restaurants.length, searchTerm });

        if (!searchTerm.trim()) {
          return restaurants; // Pas de filtre = tous les restaurants
        }

        const term = searchTerm.toLowerCase().trim();
        return restaurants.filter(restaurant =>
          restaurant.name.toLowerCase().includes(term) ||
          restaurant.address.toLowerCase().includes(term) ||
          (restaurant.phoneNumber && restaurant.phoneNumber.includes(term))
        );
      }),
      takeUntil(this.destroy$) // Auto-cleanup pour éviter les memory leaks
    );
  }

  ngOnInit(): void {
    console.log('🚀 RestaurantListComponent - ngOnInit');

    // Charge les données (une seule fois !)
    this.restaurantService.loadRestaurants();

    // Debug: observe les changements de search
    this.searchTerm$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(term => {
      console.log('🔍 Search term changed:', term);
    });

    // Debug: observe les résultats filtrés
    this.filteredRestaurants$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(filtered => {
      console.log('📋 Filtered restaurants:', filtered.length);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ===== ACTIONS RÉACTIVES =====

  /**
   * 🎯 Méthode appelée par le template
   * Notice: PAS de manipulation directe de données !
   */
  onSearch(searchTerm: string): void {
    console.log('🔍 User search input:', searchTerm);
    this.searchTerm$.next(searchTerm);
    // C'est tout ! Le reste se fait automatiquement grâce aux streams 🚀
  }

  /**
   * 🎯 Sélection d'un restaurant
   */
  selectRestaurant(restaurant: Restaurant): void {
    console.log('🍽️ Restaurant selected:', restaurant.name);
    // Ici tu pourrais naviguer vers les détails ou ouvrir un modal
     this.router.navigate(['/restaurants', restaurant.id]);
  }

  /**
   * 🎯 Action pour réserver (future feature)
   */
  makeReservation(restaurant: Restaurant): void {
    console.log('📅 Make reservation for:', restaurant.name);
    // Ici tu navigueras vers le formulaire de réservation
    // this.router.navigate(['/booking'], { queryParams: { restaurantId: restaurant.id } });
  }

  // ===== GETTERS UTILES POUR LE TEMPLATE =====

  /**
   * 🎯 Getter synchrone pour obtenir le terme de recherche actuel
   */
  get currentSearchTerm(): string {
    return this.searchTerm$.value;
  }
}
