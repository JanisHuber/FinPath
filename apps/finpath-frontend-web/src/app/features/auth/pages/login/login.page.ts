import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.page.html',
})
export class LoginPage {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.error = '';
    this.loading = true;

    this.authService.login(this.email, this.password).subscribe({
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
        this.error = err.message || 'Login fehlgeschlagen';
      },
    });
  }
}
