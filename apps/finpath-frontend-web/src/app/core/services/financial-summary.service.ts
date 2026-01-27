import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FinancialSummary } from '@models/financial-summary.models';
import { Observable } from 'rxjs';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class FinancialSummaryService {

  constructor(private http: HttpClient) {}

  getFinancialSummary(): Observable<FinancialSummary> {
    return this.http.get<FinancialSummary>(`${environment.apiBaseUrl}/financial-summary`);
  }
}
