import { Restaurant } from "./restaurant.model";

export interface DiningTable {
  id: number;
  capacity: number;
  status: string;
  restaurant: Restaurant;
}
