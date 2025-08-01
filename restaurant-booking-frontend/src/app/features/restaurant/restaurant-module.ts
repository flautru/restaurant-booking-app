import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RestaurantRoutingModule } from './restaurant-routing-module';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RestaurantListComponent } from './ui/restaurant-list/restaurant-list.component';
import { RestaurantItemComponent } from './ui/restaurant-item/restaurant-item.component';

@NgModule({
  declarations: [RestaurantListComponent, RestaurantItemComponent],
  imports: [
    CommonModule,
    RestaurantRoutingModule,
    MatListModule,
    MatProgressSpinnerModule,
  ],
})
export class RestaurantModule {}
