import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env';
import {
  LearningModule,
  LearningModuleSummary,
  LearningProgress,
  LearningStats,
  LearningCategory,
  LearningStatus,
  UpdateProgressRequest
} from '@models/learning.models';

@Injectable({
  providedIn: 'root'
})
export class LearningService {
  private readonly baseUrl = `${environment.apiBaseUrl}/learning`;

  constructor(private http: HttpClient) {}

  // ==================== Module Operations ====================

  getModules(category?: LearningCategory): Observable<LearningModuleSummary[]> {
    let params = new HttpParams();
    if (category) {
      params = params.set('category', category);
    }
    return this.http.get<LearningModuleSummary[]>(`${this.baseUrl}/modules`, { params });
  }

  getModule(id: string): Observable<LearningModule> {
    return this.http.get<LearningModule>(`${this.baseUrl}/modules/${id}`);
  }

  // ==================== Progress Operations ====================

  getProgress(status?: LearningStatus): Observable<LearningProgress[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<LearningProgress[]>(`${this.baseUrl}/progress`, { params });
  }

  updateProgress(moduleId: string, request: UpdateProgressRequest): Observable<LearningProgress> {
    return this.http.post<LearningProgress>(`${this.baseUrl}/progress/${moduleId}`, request);
  }

  startModule(moduleId: string): Observable<LearningProgress> {
    return this.http.post<LearningProgress>(`${this.baseUrl}/modules/${moduleId}/start`, {});
  }

  completeModule(moduleId: string): Observable<LearningProgress> {
    return this.http.post<LearningProgress>(`${this.baseUrl}/modules/${moduleId}/complete`, {});
  }

  // ==================== Stats Operations ====================

  getStats(): Observable<LearningStats> {
    return this.http.get<LearningStats>(`${this.baseUrl}/stats`);
  }
}
