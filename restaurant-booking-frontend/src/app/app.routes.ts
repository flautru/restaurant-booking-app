import { Routes } from '@angular/router';


export const routes: Routes = [
  {
    path: 'restaurants',
    loadChildren: () => import('./features/restaurant/restaurant-module').then(m => m.RestaurantModule)
  },
  { path: '', redirectTo: 'restaurants', pathMatch: 'full' },
];
