import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.page.html',
})
export class RegisterPage implements OnInit {
  registerForm!: FormGroup;
  error = '';
  success = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  get f() {
    return this.registerForm.controls;
  }

  getFieldError(fieldName: string): string | null {
    const field = this.registerForm.get(fieldName);
    if (!field || !field.errors || !field.touched) return null;

    if (field.errors['required']) return 'Dieses Feld ist erforderlich';
    if (field.errors['email']) return 'Ungültige E-Mail-Adresse';
    if (field.errors['minlength']) {
      const minLength = field.errors['minlength'].requiredLength;
      return `Mindestens ${minLength} Zeichen erforderlich`;
    }
    if (field.errors['maxlength']) {
      const maxLength = field.errors['maxlength'].requiredLength;
      return `Maximal ${maxLength} Zeichen erlaubt`;
    }
    return null;
  }

  onSubmit() {
    this.error = '';
    this.success = '';

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      if (this.registerForm.errors?.['passwordMismatch']) {
        this.error = 'Passwörter stimmen nicht überein';
      }
      return;
    }

    this.loading = true;
    const { email, password, username } = this.registerForm.value;

    this.authService.register(email, password, username).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.error) {
          this.error = response.error.message;
        } else {
          this.success = 'Registrierung erfolgreich! Bitte bestätige deine E-Mail.';
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err.apiError?.message || err.message || 'Registrierung fehlgeschlagen';
      },
    });
  }
}
