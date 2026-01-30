CREATE TYPE account_type AS ENUM ('savings', 'checking', 'investment', 'cash', 'pension_3a');

CREATE TABLE public.accounts (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    account_type account_type NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES auth.users(id)
            ON DELETE CASCADE
);

-- Trigger to update updated_at on row update
CREATE OR REPLACE FUNCTION public.set_account_updated_at()
RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER accounts_set_updated_at
    BEFORE UPDATE ON public.accounts
    FOR EACH ROW
    EXECUTE FUNCTION public.set_account_updated_at();

-- Enable Row Level Security
ALTER TABLE public.accounts ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can view own accounts"
    ON public.accounts FOR SELECT
    USING ((select auth.uid()) = user_id);
CREATE POLICY "Users can insert own accounts"
    ON public.accounts FOR INSERT
    WITH CHECK ((select auth.uid()) = user_id);
CREATE POLICY "Users can update own accounts"
    ON public.accounts FOR UPDATE
    USING ((select auth.uid()) = user_id);