import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '@core/services/auth.service';
import { Profile } from '@models/profile.models';

@Component({
  standalone: true,
  selector: 'app-settings-profile-page',
  imports: [CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.css'],
})
export class SettingsProfilePage implements OnInit {
  profile: Profile | null = null;
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  isLoading = true;
  isSaving = false;
  isChangingPassword = false;
  showDeleteConfirm = false;
  deleteConfirmText = '';
  error: string | null = null;
  successMessage: string | null = null;

  private fb = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.initForms();
    this.loadProfile();
  }

  private initForms(): void {
    this.profileForm = this.fb.group({
      displayName: ['', [Validators.required, Validators.maxLength(100)]]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmNewPassword: ['', Validators.required]
    });
  }

  private loadProfile(): void {
    this.isLoading = true;
    this.authService.currentProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (profile) => {
          this.profile = profile;
          if (profile) {
            this.profileForm.patchValue({
              displayName: profile.displayName
            });
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading profile:', error);
          this.error = 'Failed to load profile';
          this.isLoading = false;
        }
      });
  }

  onSaveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.error = null;
    this.successMessage = null;

    const displayName = this.profileForm.get('displayName')?.value;

    this.authService.updateProfile({ displayName })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.successMessage = 'Profile updated successfully';
          this.isSaving = false;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.error = 'Failed to update profile';
          this.isSaving = false;
        }
      });
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const { newPassword, confirmNewPassword } = this.passwordForm.value;
    if (newPassword !== confirmNewPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.isChangingPassword = true;
    this.error = null;
    this.successMessage = null;

    this.authService.updatePassword(newPassword)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.successMessage = 'Password changed successfully';
          this.passwordForm.reset();
          this.isChangingPassword = false;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (error) => {
          console.error('Error changing password:', error);
          this.error = 'Failed to change password';
          this.isChangingPassword = false;
        }
      });
  }

  onDeleteAccount(): void {
    if (this.deleteConfirmText.toUpperCase() !== 'DELETE' && this.deleteConfirmText.toUpperCase() !== 'LOSCHEN') {
      return;
    }

    // TODO: Implement account deletion via AuthService
    console.log('Account deletion requested');
  }

  getInitials(): string {
    if (!this.profile?.displayName) return '?';
    return this.profile.displayName
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString();
  }
}