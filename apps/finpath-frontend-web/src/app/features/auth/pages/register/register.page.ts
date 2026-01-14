import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.page.html',
})
export class RegisterPage {
  username = '';
  email = '';
  password = '';
  confirmPassword = '';
  error = '';
  success = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.error = '';
    this.success = '';

    if (this.password !== this.confirmPassword) {
      this.error = 'Passwörter stimmen nicht überein';
      return;
    }

    this.loading = true;

    this.authService.register(this.email, this.password, this.username).subscribe({
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
        this.error = err.message || 'Registrierung fehlgeschlagen';
      },
    });
  }
}
