import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RestaurantListComponent } from './ui/restaurant-list/restaurant-list.component';
import { RestaurantDetailComponent } from './ui/restaurant-detail/restaurant-detail.component';
import { DiningTableListComponent } from './ui/dining-table-list/dining-table-list.component';

const routes: Routes = [
  { path: '', component: RestaurantListComponent },
  { path: ':id', component: RestaurantDetailComponent },
  { path: ':id/tables', component: DiningTableListComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RestaurantRoutingModule { }
