import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CheckIn } from '@models/check-in.models';
import { Observable } from 'rxjs';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class CheckInService {

  constructor(private http: HttpClient) {}

  getCheckIn(): Observable<CheckIn> {
    return this.http.get<CheckIn>(`${environment.apiBaseUrl}/check-in`);
  }
}
