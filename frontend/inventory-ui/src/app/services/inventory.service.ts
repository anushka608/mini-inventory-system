import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Inventory {
  id: number;
  sku: string;
  mrp: number;
  batchNo: string;
  quantity: number;
  status: string;
  expiryDate: string;
}

export interface InventorySummary {
  sku: string;
  goodQty: number;
  damagedQty: number;
  expiredQty: number;
}

@Injectable({ providedIn: 'root' })
export class InventoryService {

  private api1 = 'http://localhost:8080/api/inventory/all';
  private api2 = 'http://localhost:8080/api/inventory/summary';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(this.api1);
  }
  getSummary() {
  return this.http.get<InventorySummary[]>(this.api2);
}
}