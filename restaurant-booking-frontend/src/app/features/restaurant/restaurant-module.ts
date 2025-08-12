import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RestaurantRoutingModule } from './restaurant-routing-module';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RestaurantListComponent } from './ui/restaurant-list/restaurant-list.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { RestaurantDetailComponent } from './ui/restaurant-detail/restaurant-detail.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { DiningTableListComponent } from './ui/dining-table-list/dining-table-list.component';

@NgModule({
  declarations: [RestaurantListComponent, RestaurantDetailComponent, DiningTableListComponent],
  imports: [
    CommonModule,
    RestaurantRoutingModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatListModule,
    MatMenuModule,
    MatDividerModule
  ],
})
export class RestaurantModule {}
