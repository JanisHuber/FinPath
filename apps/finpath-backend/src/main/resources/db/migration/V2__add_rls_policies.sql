-- FinPath Row Level Security (RLS) Policies
-- Version 2.0
-- This migration enables RLS and creates policies for all tables

-- ==================================================
-- Enable RLS on all tables
-- ==================================================

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE accounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE budget_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE financial_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE milestones ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE learning_modules ENABLE ROW LEVEL SECURITY;
ALTER TABLE learning_progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE family_members ENABLE ROW LEVEL SECURITY;

-- ==================================================
-- Profiles Table Policies
-- ==================================================

-- Users can only view their own profile
CREATE POLICY "Users can view own profile"
    ON profiles FOR SELECT
    USING (auth.uid() = id);

-- Users can update their own profile
CREATE POLICY "Users can update own profile"
    ON profiles FOR UPDATE
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

-- Users can insert their own profile (for registration)
CREATE POLICY "Users can insert own profile"
    ON profiles FOR INSERT
    WITH CHECK (auth.uid() = id);

-- ==================================================
-- User Settings Table Policies
-- ==================================================

CREATE POLICY "Users can view own settings"
    ON user_settings FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can update own settings"
    ON user_settings FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can insert own settings"
    ON user_settings FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own settings"
    ON user_settings FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Accounts Table Policies
-- ==================================================

CREATE POLICY "Users can view own accounts"
    ON accounts FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create own accounts"
    ON accounts FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own accounts"
    ON accounts FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own accounts"
    ON accounts FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Transactions Table Policies
-- ==================================================

CREATE POLICY "Users can view own transactions"
    ON transactions FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create own transactions"
    ON transactions FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own transactions"
    ON transactions FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own transactions"
    ON transactions FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Budget Goals Table Policies
-- ==================================================

CREATE POLICY "Users can view own budget goals"
    ON budget_goals FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create own budget goals"
    ON budget_goals FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own budget goals"
    ON budget_goals FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own budget goals"
    ON budget_goals FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Financial Goals Table Policies
-- ==================================================

CREATE POLICY "Users can view own financial goals"
    ON financial_goals FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create own financial goals"
    ON financial_goals FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own financial goals"
    ON financial_goals FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own financial goals"
    ON financial_goals FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Milestones Table Policies
-- Milestones are accessed through their parent financial_goal
-- ==================================================

CREATE POLICY "Users can view own milestones"
    ON milestones FOR SELECT
    USING (
        EXISTS (
            SELECT 1 FROM financial_goals fg
            WHERE fg.id = milestones.goal_id
            AND fg.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can create milestones for own goals"
    ON milestones FOR INSERT
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM financial_goals fg
            WHERE fg.id = goal_id
            AND fg.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can update own milestones"
    ON milestones FOR UPDATE
    USING (
        EXISTS (
            SELECT 1 FROM financial_goals fg
            WHERE fg.id = milestones.goal_id
            AND fg.user_id = auth.uid()
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM financial_goals fg
            WHERE fg.id = goal_id
            AND fg.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can delete own milestones"
    ON milestones FOR DELETE
    USING (
        EXISTS (
            SELECT 1 FROM financial_goals fg
            WHERE fg.id = milestones.goal_id
            AND fg.user_id = auth.uid()
        )
    );

-- ==================================================
-- Notifications Table Policies
-- ==================================================

CREATE POLICY "Users can view own notifications"
    ON notifications FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can update own notifications"
    ON notifications FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own notifications"
    ON notifications FOR DELETE
    USING (auth.uid() = user_id);

-- System can insert notifications for any user (via service role)
CREATE POLICY "Service can create notifications"
    ON notifications FOR INSERT
    WITH CHECK (true);

-- ==================================================
-- Learning Modules Table Policies
-- Learning modules are public read for all authenticated users
-- ==================================================

CREATE POLICY "Authenticated users can view published modules"
    ON learning_modules FOR SELECT
    USING (is_published = true AND auth.role() = 'authenticated');

-- Admin-only policies would require a separate admin role
-- For now, modules are managed via service role

-- ==================================================
-- Learning Progress Table Policies
-- ==================================================

CREATE POLICY "Users can view own learning progress"
    ON learning_progress FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create own learning progress"
    ON learning_progress FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own learning progress"
    ON learning_progress FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own learning progress"
    ON learning_progress FOR DELETE
    USING (auth.uid() = user_id);

-- ==================================================
-- Family Members Table Policies
-- ==================================================

CREATE POLICY "Users can view own family members"
    ON family_members FOR SELECT
    USING (auth.uid() = owner_user_id);

CREATE POLICY "Users can create own family members"
    ON family_members FOR INSERT
    WITH CHECK (auth.uid() = owner_user_id);

CREATE POLICY "Users can update own family members"
    ON family_members FOR UPDATE
    USING (auth.uid() = owner_user_id)
    WITH CHECK (auth.uid() = owner_user_id);

CREATE POLICY "Users can delete own family members"
    ON family_members FOR DELETE
    USING (auth.uid() = owner_user_id);

-- ==================================================
-- Grant Permissions to authenticated role
-- ==================================================

GRANT SELECT, INSERT, UPDATE, DELETE ON profiles TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON user_settings TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON transactions TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON budget_goals TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON financial_goals TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON milestones TO authenticated;
GRANT SELECT, UPDATE, DELETE ON notifications TO authenticated;
GRANT SELECT ON learning_modules TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON learning_progress TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON family_members TO authenticated;

-- Service role has full access for backend operations
GRANT ALL ON ALL TABLES IN SCHEMA public TO service_role;
