package rw.utility.billing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Central source for notification wording so the email channel matches the
 * database-trigger wording in db/routines.sql.
 */
public final class NotificationMessages {

    private NotificationMessages() {}

    public static String period(int month, int year) {
        return String.format("%02d/%d", month, year);
    }

    private static String amount(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String billProcessed(String customerName, int month, int year, BigDecimal total) {
        return "Dear " + customerName + ", Your " + period(month, year)
                + " utility bill of " + amount(total) + " FRW has been successfully processed.";
    }

    public static String paymentCompleted(String customerName, String billReference, BigDecimal total) {
        return "Dear " + customerName + ", Your payment for bill " + billReference
                + " of " + amount(total) + " FRW has been received in full. Your bill is now settled.";
    }
}
