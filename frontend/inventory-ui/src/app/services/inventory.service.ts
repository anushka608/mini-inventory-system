import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';

export interface Inventory {
  sku:string;
  mrp:number;
  batchNo:string;
  quantity:number;
  status:string;
  expiryDate:string;
}

export interface InventorySummary{
  sku:string;
  goodQty:number;
  damagedQty:number;
  expiredQty:number;
}

@Injectable({providedIn:'root'})
export class InventoryService{

  private base='http://localhost:8080/api/inventory';

  constructor(private http:HttpClient){}

  getAll():Observable<Inventory[]>{
  return this.http.get<Inventory[]>(`${this.base}/all`);
}

getSummary():Observable<InventorySummary[]>{
  return this.http.get<InventorySummary[]>(`${this.base}/summary`);
}
}