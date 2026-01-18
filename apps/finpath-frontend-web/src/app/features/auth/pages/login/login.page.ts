import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.page.html',
})
export class LoginPage implements OnInit {
  loginForm!: FormGroup;
  error = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  getFieldError(fieldName: string): string | null {
    const field = this.loginForm.get(fieldName);
    if (!field || !field.errors || !field.touched) return null;

    if (field.errors['required']) return 'Dieses Feld ist erforderlich';
    if (field.errors['email']) return 'Ungültige E-Mail-Adresse';
    return null;
  }

  onSubmit() {
    this.error = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.error) {
          this.error = response.error.message;
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err.apiError?.message || err.message || 'Login fehlgeschlagen';
      },
    });
  }
}
