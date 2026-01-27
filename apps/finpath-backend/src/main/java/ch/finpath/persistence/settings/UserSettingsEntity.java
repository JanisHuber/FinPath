package ch.finpath.persistence.settings;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
public class UserSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "language", nullable = false, length = 5)
    private String language = "de";

    @Column(name = "theme", nullable = false, length = 20)
    private String theme = "system";

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "CHF";

    @Column(name = "notifications_in_app", nullable = false)
    private boolean notificationsInApp = true;

    @Column(name = "notifications_email", nullable = false)
    private boolean notificationsEmail = true;

    @Column(name = "notifications_push", nullable = false)
    private boolean notificationsPush = false;

    @Column(name = "privacy_analytics", nullable = false)
    private boolean privacyAnalytics = true;

    @Column(name = "privacy_personalization", nullable = false)
    private boolean privacyPersonalization = true;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public UserSettingsEntity() {
    }

    public UserSettingsEntity(UUID userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isNotificationsInApp() {
        return notificationsInApp;
    }

    public void setNotificationsInApp(boolean notificationsInApp) {
        this.notificationsInApp = notificationsInApp;
    }

    public boolean isNotificationsEmail() {
        return notificationsEmail;
    }

    public void setNotificationsEmail(boolean notificationsEmail) {
        this.notificationsEmail = notificationsEmail;
    }

    public boolean isNotificationsPush() {
        return notificationsPush;
    }

    public void setNotificationsPush(boolean notificationsPush) {
        this.notificationsPush = notificationsPush;
    }

    public boolean isPrivacyAnalytics() {
        return privacyAnalytics;
    }

    public void setPrivacyAnalytics(boolean privacyAnalytics) {
        this.privacyAnalytics = privacyAnalytics;
    }

    public boolean isPrivacyPersonalization() {
        return privacyPersonalization;
    }

    public void setPrivacyPersonalization(boolean privacyPersonalization) {
        this.privacyPersonalization = privacyPersonalization;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
