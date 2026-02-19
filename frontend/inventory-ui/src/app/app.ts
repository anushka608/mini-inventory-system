import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector:'app-root',
  standalone:true,
  imports:[RouterOutlet],
  template:`

  <div class="topbar">
      <div class="brand">
          <div class="brand-logo">WH</div>
          Warehouse Management
      </div>
<!-- 
      <div>● Live &nbsp;&nbsp; Admin</div> -->
  </div>

  <router-outlet></router-outlet>
  `
})
export class App{}