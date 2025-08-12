import { Component } from '@angular/core';
import { DiningTable } from '../../model/dining-table.model';
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  map,
  Observable,
  startWith,
  Subject,
  takeUntil,
} from 'rxjs';
import { DiningTableQuery } from '../../state/dining-table.query';
import { DiningTableService } from '../../state/dining-table.service';
import { Router } from '@angular/router';
import { RestaurantService } from '../../state/restaurant.service';
import { RestaurantQuery } from '../../state/restaurant.query';
import { Restaurant } from '../../model/restaurant.model';

@Component({
  selector: 'app-dining-table-list',
  templateUrl: './dining-table-list.component.html',
  styleUrl: './dining-table-list.component.scss',
  standalone: false,
})
export class DiningTableListComponent {
  tables$: Observable<DiningTable[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;
  activeRestaurant$: Observable<Restaurant | undefined>;

  // Search stream - le cœur du pattern réactif !
  private searchTerm$ = new BehaviorSubject<string>('');

  // Stream dérivé - automatiquement synchronisé !
  filteredTables$: Observable<DiningTable[]>;

  // Cleanup stream
  private destroy$ = new Subject<void>();

  constructor(
    private tableQuery: DiningTableQuery,
    private tableService: DiningTableService,
    private restaurantQuery: RestaurantQuery,
    private router: Router
  ) {
    // Initialisation des streams après l'assignation de tableQuery
    this.tables$ = this.tableQuery.selectAll();
    this.loading$ = this.tableQuery.selectLoading();
    this.error$ = this.tableQuery.selectError();
    this.activeRestaurant$ = this.restaurantQuery.selectActive() as Observable<Restaurant | undefined>;

    this.filteredTables$ = combineLatest([
      this.tables$,
      this.searchTerm$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        startWith('') // Commence avec une recherche vide
      ),
    ]).pipe(
      map(([tables, searchTerm]) => {
        console.log('🔍 Filtering:', {
          tablesCount: tables.length,
          searchTerm,
        });

        if (!searchTerm.trim()) {
          return tables; // Pas de filtre = tous les restaurants
        }

        const term = searchTerm.toLowerCase().trim();
        return tables.filter((table) =>
          table.capacity.toString().includes(term)
        );
      }),
      takeUntil(this.destroy$) // Auto-cleanup pour éviter les memory leaks
    );
  }

  ngOnInit(): void {
    console.log('🚀 RestaurantListComponent - ngOnInit');

    // Charge les données (une seule fois !)
    const activeRestaurantId = this.restaurantQuery.getActiveId();

    this.tableService.loadDiningTableForRestaurant(activeRestaurantId);

    // Debug: observe les changements de search
    this.searchTerm$.pipe(takeUntil(this.destroy$)).subscribe((term) => {
      console.log('🔍 Search term changed:', term);
    });

    // // Debug: observe les résultats filtrés
    // this.filteredRestaurants$.pipe(
    //   takeUntil(this.destroy$)
    // ).subscribe(filtered => {
    //   console.log('📋 Filtered restaurants:', filtered.length);
    // });
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
  selectTable(table: DiningTable): void {
    console.log(
      '🍽️ Restaurant selected:',
      table.restaurant.name,
      'table : ',
      table.id
    );
    // Ici tu pourrais naviguer vers les détails ou ouvrir un modal
  }

  /**
   * 🎯 Action pour réserver (future feature)
   */
  makeReservation(table: DiningTable): void {
    console.log(
      '📅 Make reservation for:',
      table.restaurant.name,
      'table :',
      table.id
    );
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
