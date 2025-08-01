import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Restaurant } from '../../model/restaurant.model';
import { RestaurantQuery } from '../../state/restaurant.query';
import { RestaurantService } from '../../state/restaurant.service';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.scss'],
  standalone: false // Composant non autonome, dépend du module RestaurantModule
})
export class RestaurantListComponent implements OnInit {
  restaurants$!: Observable<Restaurant[]> // Observable liste restaurants
  loading$!: Observable<boolean>;         // Observable chargement
  error$!: Observable<string>;            // Observable erreur
  constructor(
    private restaurantQuery: RestaurantQuery,
    private restaurantService: RestaurantService
  ) {console.log('🏗️ RestaurantListComponent - Constructor');}

  ngOnInit(): void {
console.log('🚀 RestaurantListComponent - ngOnInit START');

  try {
    console.log('📊 Setting up observables...');
    this.restaurants$ = this.restaurantQuery.selectAll();
    this.loading$ = this.restaurantQuery.selectLoading();

    console.log('📞 Calling loadRestaurants...');
    this.restaurantService.loadRestaurants();

    console.log('✅ ngOnInit completed successfully');
  } catch (error) {
    console.error('❌ Error in ngOnInit:', error);
  }
  }


}
