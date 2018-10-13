package tz.co.neelansoft.presis.helpers;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppConstants {

    public static final String dateStringFormat = "dd MMM, yyyy";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateStringFormat, Locale.getDefault());
    public static final String FIREBASE_ROOT = "presis";
    public static final int SALE = 1;
    public static final int PURCHASE = 2;
    public static final int EXPENSE = 0;
}
