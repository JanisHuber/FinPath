import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FinancialScore } from '@models/financial-score.models';
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FinancialScoreService {

  constructor(private http: HttpClient) {}

  getFinancialScore(token: string): Observable<FinancialScore> {
    return this.http.get<FinancialScore>('api/v1/financial-score', {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }),
    });
  }
}
