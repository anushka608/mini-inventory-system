import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { InventoryService, Inventory } from '../services/inventory.service';
import { CommonModule } from '@angular/common';

@Component({
  selector:'app-dashboard',
  standalone:true,
  imports:[CommonModule],
  templateUrl:'./dashboard.component.html'
})
export class DashboardComponent implements OnInit{

  inventory:Inventory[]=[];
  loading=true;

  constructor(
    private service:InventoryService,
    private cd:ChangeDetectorRef
  ){}

  ngOnInit(){
    this.service.getAll().subscribe({
      next:data=>{
        this.inventory=data||[];
        this.loading=false;
        this.cd.detectChanges();
      },
      error:err=>{
        console.error(err);
        this.loading=false;
        this.cd.detectChanges();
      }
    });
  }
}