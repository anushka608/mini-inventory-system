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

@Injectable({ providedIn: 'root' })
export class InventoryService {

  private api = 'http://localhost:8080/api/inventory/all';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(this.api);
  }
}