import { Injectable } from '@angular/core';
import { environment } from '@env';
import { AuthChangeEvent, AuthResponse, createClient, Session, SupabaseClient, User } from '@supabase/supabase-js';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { Profile } from '@models/profile.models'
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private supabase: SupabaseClient;
  private currentUserSubject: BehaviorSubject<User | null>;
  private currentProfileSubject: BehaviorSubject<Profile | null>;
  private initializedSubject = new BehaviorSubject<boolean>(false);

  public currentUser$: Observable<User | null>;
  public currentProfile$: Observable<Profile | null>;
  public isInitialized$ = this.initializedSubject.asObservable();

  private http: HttpClient;

  constructor(private readonly httpClient: HttpClient) {
    this.http = httpClient;
    this.supabase = createClient(environment.supabaseUrl, environment.supabaseKey);
    this.currentUserSubject = new BehaviorSubject<User | null>(null);
    this.currentUser$ = this.currentUserSubject.asObservable();
    this.currentProfileSubject = new BehaviorSubject<Profile | null>(null);
    this.currentProfile$ = this.currentProfileSubject.asObservable();

    this.initializeAuth();
  }

  private async initializeAuth() {
    const { data } = await this.supabase.auth.getSession();
    this.currentUserSubject.next(data.session?.user ?? null);
    this.initializedSubject.next(true);

    this.supabase.auth.onAuthStateChange((_event: AuthChangeEvent, session: Session | null) => {
      this.currentUserSubject.next(session?.user ?? null);
      if (session?.user) {
        this.http.get<Profile>(`${environment.apiBaseUrl}/me`).subscribe({
          next: (profile) => {
            this.currentProfileSubject.next(profile);
            console.log("User Profile loaded: " + JSON.stringify(profile));
          },
          error: (err) => console.error("Failed to load profile:", err)
        });
      } else {
        this.currentProfileSubject.next(null);
      }
    });
  }

  async getAccessToken(): Promise<string | null> {
    const { data } = await this.supabase.auth.getSession();
    return data.session?.access_token ?? null;
  }

  register(email: string, password: string, username: string): Observable<AuthResponse> {
    const promise = this.supabase.auth.signUp({
      email: email,
      password: password,
      options: {
        data: {
          username: username,
        },
      },
    });
    return from(promise);
  }

  login(email: string, password: string): Observable<AuthResponse> {
    const promise = this.supabase.auth.signInWithPassword({
      email: email,
      password: password,
    });
    return from(promise);
  }

  async logout() {
    const { error } = await this.supabase.auth.signOut();
    if (!error) {
      this.currentUserSubject.next(null);
    }
    return { error };
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getCurrentProfile(): Profile | null {
    return this.currentProfileSubject.value;
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }
}
