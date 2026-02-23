import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Inventory {
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

/* matches backend ApiResponse<T> wrapper */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class InventoryService {

  constructor(private http: HttpClient) { }

  /* unwrap response.data */
  private base = 'http://localhost:8080/api/inventory';

  getAll() {
    return this.http.get<Inventory[]>(`${this.base}/all`);
  }

  getSummary() {
    return this.http.get<InventorySummary[]>(`${this.base}/summary`);
  }
}