import { Routes } from '@angular/router';


export const routes: Routes = [
  {
    path: 'restaurants',
    loadChildren: () => import('./features/restaurant/restaurant-module').then(m => m.RestaurantModule)
  },
  {
    path: 'customers',
    loadChildren: () => import('./features/customer/customer-module').then(m => m.CustomerModule)
  },
  { path: '', redirectTo: 'restaurants', pathMatch: 'full' },
];
