import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BudgetGoals } from '@models/budget-goals.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BudgetGoalsService {

  constructor(private http: HttpClient) {}

  getBudgetGoals(): Observable<BudgetGoals> {
    return this.http.get<BudgetGoals>('/api/v1/budget-goals');
  }
}
