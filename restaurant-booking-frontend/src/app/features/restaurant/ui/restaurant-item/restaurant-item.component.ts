import { Component, Input } from '@angular/core';
import { Restaurant } from '../../model/restaurant.model';

@Component({
  selector: 'app-restaurant-item',
  templateUrl: './restaurant-item.component.html',
  styleUrls: ['./restaurant-item.component.scss'],
  standalone: false // Composant non autonome, dépend du module RestaurantModule
})
export class RestaurantItemComponent {
  @Input() restaurant!: Restaurant;
}
