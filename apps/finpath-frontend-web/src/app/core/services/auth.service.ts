import { Injectable } from '@angular/core';
import { environment } from '@env';
import { AuthChangeEvent, AuthResponse, createClient, Session, SupabaseClient, User } from '@supabase/supabase-js';
import { BehaviorSubject, from, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private supabase: SupabaseClient;
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor() {
    this.supabase = createClient(environment.supabaseUrl, environment.supabaseKey);
    this.currentUserSubject = new BehaviorSubject<User | null>(null);
    this.currentUser$ = this.currentUserSubject.asObservable();

    this.initializeAuth();
  }

  private async initializeAuth() {
    const { data } = await this.supabase.auth.getSession();
    this.currentUserSubject.next(data.session?.user ?? null);

    this.supabase.auth.onAuthStateChange((_event: AuthChangeEvent, session: Session | null) => {
      this.currentUserSubject.next(session?.user ?? null);
    });
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

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }
}
