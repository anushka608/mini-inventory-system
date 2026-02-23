import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { InventoryService, Inventory, InventorySummary } from '../services/inventory.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  summary: InventorySummary[] = [];
  inventory: Inventory[] = [];
  filteredInventory: Inventory[] = [];

  today: Date = new Date();

  totalGood = 0;
  totalDamaged = 0;
  totalExpired = 0;

  selectedStatus = 'ALL';
  searchText = '';

  loading = true;

  sortColumn: keyof Inventory | null = null;
  sortAsc = true;

  constructor(
  private service: InventoryService,
  private cd: ChangeDetectorRef
) {}

  ngOnInit() {

    forkJoin({
      inventory: this.service.getAll(),
      summary: this.service.getSummary()
    }).subscribe({

      next: ({ inventory, summary }) => {

        this.inventory = inventory ?? [];
        this.summary = summary ?? [];

        this.filteredInventory = [...this.inventory];

        this.totalGood =
          this.summary.reduce((a,b)=>a+(b.goodQty||0),0);

        this.totalDamaged =
          this.summary.reduce((a,b)=>a+(b.damagedQty||0),0);

        this.totalExpired =
          this.summary.reduce((a,b)=>a+(b.expiredQty||0),0);

        this.loading = false;
        this.cd.detectChanges();
      },

      error: err => {
        console.error(err);
        this.loading = false;
        
      }

    });

  }

  applyFilter() {

    const text = this.searchText.toLowerCase();

    this.filteredInventory = this.inventory.filter(item => {

      const matchesSearch =
        item.sku?.toLowerCase().includes(text) ||
        item.batchNo?.toLowerCase().includes(text);

      const matchesStatus =
        this.selectedStatus === 'ALL' ||
        item.status === this.selectedStatus;

      return matchesSearch && matchesStatus;
    });

    this.applySort();
  }

  sortBy(column: keyof Inventory) {

    if (this.sortColumn === column) {
      this.sortAsc = !this.sortAsc;
    } else {
      this.sortColumn = column;
      this.sortAsc = true;
    }

    this.applySort();
  }

  getSortIcon(column: keyof Inventory) {

    if (this.sortColumn !== column) return '↕';
    return this.sortAsc ? '↑' : '↓';
  }

  private applySort() {

    if (!this.sortColumn) return;

    const key = this.sortColumn;
    const asc = this.sortAsc;

    this.filteredInventory = [...this.filteredInventory].sort((a,b)=>{

      const va = a[key] as any;
      const vb = b[key] as any;

      if (key === 'expiryDate') {
        return asc
          ? new Date(va).getTime()-new Date(vb).getTime()
          : new Date(vb).getTime()-new Date(va).getTime();
      }

      if (typeof va === 'number' && typeof vb === 'number') {
        return asc ? va-vb : vb-va;
      }

      return asc
        ? String(va).localeCompare(String(vb))
        : String(vb).localeCompare(String(va));

    });
  }

  trackByBatch(index:number,item:Inventory){
    return item.sku+item.batchNo+item.mrp;
  }
}