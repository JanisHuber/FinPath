import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BudgetGoalsService {
  
  constructor(private http: HttpClient) {}

  getBudgetGoals() {
    return this.http.get('/api/v1/budget-goals');
  }
}
