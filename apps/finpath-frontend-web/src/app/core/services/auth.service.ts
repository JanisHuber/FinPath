import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthChangeEvent, AuthResponse, createClient, Session, SupabaseClient, User } from '@supabase/supabase-js';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { Profile } from '../models/profile.models';
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
    this.supabase = createClient(environment.supabaseUrl, environment.supabaseKey, {
      auth: {
        storageKey: 'sb-auth-token',
        flowType: 'pkce',
        detectSessionInUrl: true,
        persistSession: true,
      },
    });
    this.currentUserSubject = new BehaviorSubject<User | null>(null);
    this.currentUser$ = this.currentUserSubject.asObservable();
    this.currentProfileSubject = new BehaviorSubject<Profile | null>(null);
    this.currentProfile$ = this.currentProfileSubject.asObservable();

    this.initializeAuth();
  }

  private async initializeAuth() {
    const { data: { user }, error } = await this.supabase.auth.getUser();

    if (error || !user) {
      await this.supabase.auth.signOut();
      this.currentUserSubject.next(null);
    } else {
      this.currentUserSubject.next(user);
      this.loadProfile();
    }
    this.initializedSubject.next(true);

    this.supabase.auth.onAuthStateChange((event: AuthChangeEvent, session: Session | null) => {
      if (event === 'SIGNED_IN' && session?.user) {
        this.currentUserSubject.next(session.user);
        this.loadProfile();
      } else if (event === 'SIGNED_OUT') {
        this.currentUserSubject.next(null);
        this.currentProfileSubject.next(null);
      }
    });
  }

  private loadProfile() {
    this.http.get<Profile>(`${environment.apiBaseUrl}/me`).subscribe({
      next: (profile) => {
        this.currentProfileSubject.next(profile);
        console.log('User Profile loaded:', profile);
      },
      error: (err) => console.error('Failed to load profile:', err)
    });
  }

  async getAccessToken(): Promise<string | null> {
    if (!this.currentUserSubject.value) {
      return null;
    }
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
