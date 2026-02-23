import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { InventoryService, Inventory } from '../services/inventory.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventorySummary } from '../services/inventory.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  summary: InventorySummary[] = [];
  today: Date = new Date();

  totalGood = 0;
  totalDamaged = 0;
  totalExpired = 0;
  selectedStatus: string = 'ALL';
  // statusFilter: string = 'ALL';
  searchText: string = '';
  filteredInventory: any[] = [];
  inventory: Inventory[] = [];
  loading = true;

  sortColumn: string | null = null;
  sortAsc = true;

  constructor(
    private service: InventoryService,
    private cd: ChangeDetectorRef
  ) { }

ngOnInit() {

  /* getAllreturns real array */
  this.service.getAll().subscribe({
    next: data => {

      console.log("Inventory from backend:", data); //  debug

      this.inventory = data || [];
      this.filteredInventory = [...this.inventory];

      this.applySort();

      this.loading = false;
      this.cd.detectChanges();
    },
    error: err => {
      console.error("Inventory API error:", err);
      this.loading = false;
      this.cd.detectChanges();
    }
  });


  /* summary unwrapped */
  this.service.getSummary().subscribe({
    next: data => {

      console.log("Summary from backend:", data); // debug

      this.summary = data || [];

      /* totals calculation */
      this.totalGood = this.summary.reduce((a, b) => a + (b.goodQty || 0), 0);
      this.totalDamaged = this.summary.reduce((a, b) => a + (b.damagedQty || 0), 0);
      this.totalExpired = this.summary.reduce((a, b) => a + (b.expiredQty || 0), 0);

      this.cd.detectChanges();
    },
    error: err => console.error("Summary API error:", err)
  });
}

  applyFilter() {

    const text = this.searchText.toLowerCase();

    this.filteredInventory = this.inventory.filter(item => {

      const matchesSearch =
        item.sku?.toLowerCase().includes(text) ||
        item.batchNo?.toLowerCase().includes(text);

      // 👇 IMPORTANT CHANGE
      const displayStatus = this.getDisplayStatus(item);

      const matchesStatus =
        !this.selectedStatus ||
        this.selectedStatus === 'ALL' ||
        displayStatus === this.selectedStatus;

      return matchesSearch && matchesStatus;
    });
    this.applySort();
  }

  sortBy(column: string): void {
    if (this.sortColumn === column) {
      this.sortAsc = !this.sortAsc;
    } else {
      this.sortColumn = column;
      this.sortAsc = true;
    }
    this.applySort();
    this.cd.detectChanges();
  }

  getSortIcon(column: string): string {
    if (this.sortColumn !== column) return '↕';
    return this.sortAsc ? '↑' : '↓';
  }

  private applySort(): void {
    if (!this.sortColumn) return;
    const key = this.sortColumn;
    const asc = this.sortAsc;
    this.filteredInventory = [...this.filteredInventory].sort((a, b) => {
      let va: string | number | null = a[key];
      let vb: string | number | null = b[key];
      if (key === 'status') {
        va = this.getDisplayStatus(a);
        vb = this.getDisplayStatus(b);
      }
      if (va == null && vb == null) return 0;
      if (va == null) return asc ? 1 : -1;
      if (vb == null) return asc ? -1 : 1;
      if (typeof va === 'number' && typeof vb === 'number') {
        return asc ? va - vb : vb - va;
      }
      if (key === 'expiryDate' && typeof va === 'string' && typeof vb === 'string') {
        const d = asc ? 1 : -1;
        return d * (new Date(va).getTime() - new Date(vb).getTime());
      }
      const sa = String(va).toLowerCase();
      const sb = String(vb).toLowerCase();
      if (sa < sb) return asc ? -1 : 1;
      if (sa > sb) return asc ? 1 : -1;
      return 0;
    });
  }

  getDisplayStatus(item: Inventory): string {

    if (item.expiryDate) {
      const expiry = new Date(item.expiryDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      if (expiry < today) {
        return 'EXPIRED';
      }
    }

    return item.status;
  }
}