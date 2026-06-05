-- ============================================================================
--  Database-level routines (Task 6) — PL/pgSQL trigger functions + triggers.
--  Applied at startup by DatabaseRoutineInitializer (after tables exist).
--  Idempotent: CREATE OR REPLACE for functions, DROP ... IF EXISTS for triggers.
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1) On bill generation (INSERT into bills) -> insert a notification message.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_notify_on_bill_insert()
    RETURNS TRIGGER AS $$
DECLARE
    v_customer_name TEXT;
    v_period        TEXT;
BEGIN
    SELECT full_names INTO v_customer_name
    FROM customers
    WHERE id = NEW.customer_id;

    v_period := LPAD(NEW.billing_month::TEXT, 2, '0') || '/' || NEW.billing_year::TEXT;

    INSERT INTO notifications (customer_id, bill_id, message, type, status, created_at)
    VALUES (
        NEW.customer_id,
        NEW.id,
        'Dear ' || COALESCE(v_customer_name, 'Customer') || ', Your ' || v_period ||
        ' utility bill of ' || TRIM(TO_CHAR(NEW.total_amount, 'FM999999999990.00')) ||
        ' FRW has been successfully processed.',
        'BILL_GENERATED',
        'SENT',
        NOW()
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_notify_on_bill_insert ON bills;
CREATE TRIGGER trg_notify_on_bill_insert
    AFTER INSERT ON bills
    FOR EACH ROW
EXECUTE FUNCTION fn_notify_on_bill_insert();


-- ---------------------------------------------------------------------------
-- 2) On full payment (bills.outstanding_balance reaches 0) -> mark PAID and
--    notify the customer. Fires only on the transition to fully-paid.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_notify_on_full_payment()
    RETURNS TRIGGER AS $$
DECLARE
    v_customer_name TEXT;
BEGIN
    IF NEW.outstanding_balance <= 0 AND COALESCE(OLD.outstanding_balance, 0) > 0 THEN

        -- Ensure the bill is flagged as PAID at the database level.
        NEW.status := 'PAID';

        SELECT full_names INTO v_customer_name
        FROM customers
        WHERE id = NEW.customer_id;

        INSERT INTO notifications (customer_id, bill_id, message, type, status, created_at)
        VALUES (
            NEW.customer_id,
            NEW.id,
            'Dear ' || COALESCE(v_customer_name, 'Customer') ||
            ', Your payment for bill ' || NEW.bill_reference || ' of ' ||
            TRIM(TO_CHAR(NEW.total_amount, 'FM999999999990.00')) ||
            ' FRW has been received in full. Your bill is now settled.',
            'PAYMENT_COMPLETED',
            'SENT',
            NOW()
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_notify_on_full_payment ON bills;
CREATE TRIGGER trg_notify_on_full_payment
    BEFORE UPDATE ON bills
    FOR EACH ROW
EXECUTE FUNCTION fn_notify_on_full_payment();
