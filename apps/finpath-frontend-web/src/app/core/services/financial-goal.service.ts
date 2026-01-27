import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env';
import {
  FinancialGoal,
  CreateGoalRequest,
  UpdateGoalRequest,
  Milestone,
  CreateMilestoneRequest,
  UpdateMilestoneRequest,
  GoalStatus,
  GoalCategory
} from '@models/financial-goal.models';

@Injectable({
  providedIn: 'root'
})
export class FinancialGoalService {
  private readonly baseUrl = `${environment.apiBaseUrl}/goals`;

  constructor(private http: HttpClient) {}

  // ==================== Goal Operations ====================

  getGoals(status?: GoalStatus, category?: GoalCategory): Observable<FinancialGoal[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    if (category) {
      params = params.set('category', category);
    }
    return this.http.get<FinancialGoal[]>(this.baseUrl, { params });
  }

  getGoal(id: string): Observable<FinancialGoal> {
    return this.http.get<FinancialGoal>(`${this.baseUrl}/${id}`);
  }

  createGoal(request: CreateGoalRequest): Observable<FinancialGoal> {
    return this.http.post<FinancialGoal>(this.baseUrl, request);
  }

  updateGoal(id: string, request: UpdateGoalRequest): Observable<FinancialGoal> {
    return this.http.put<FinancialGoal>(`${this.baseUrl}/${id}`, request);
  }

  deleteGoal(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  addContribution(goalId: string, amount: number): Observable<FinancialGoal> {
    return this.http.post<FinancialGoal>(`${this.baseUrl}/${goalId}/contribute`, { amount });
  }

  // ==================== Milestone Operations ====================

  getMilestones(goalId: string): Observable<Milestone[]> {
    return this.http.get<Milestone[]>(`${this.baseUrl}/${goalId}/milestones`);
  }

  getMilestone(goalId: string, milestoneId: string): Observable<Milestone> {
    return this.http.get<Milestone>(`${this.baseUrl}/${goalId}/milestones/${milestoneId}`);
  }

  createMilestone(goalId: string, request: CreateMilestoneRequest): Observable<Milestone> {
    return this.http.post<Milestone>(`${this.baseUrl}/${goalId}/milestones`, request);
  }

  updateMilestone(goalId: string, milestoneId: string, request: UpdateMilestoneRequest): Observable<Milestone> {
    return this.http.put<Milestone>(`${this.baseUrl}/${goalId}/milestones/${milestoneId}`, request);
  }

  markMilestoneAchieved(goalId: string, milestoneId: string, achieved: boolean): Observable<Milestone> {
    return this.http.put<Milestone>(
      `${this.baseUrl}/${goalId}/milestones/${milestoneId}/achieved`,
      null,
      { params: { achieved: achieved.toString() } }
    );
  }

  deleteMilestone(goalId: string, milestoneId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${goalId}/milestones/${milestoneId}`);
  }
}
