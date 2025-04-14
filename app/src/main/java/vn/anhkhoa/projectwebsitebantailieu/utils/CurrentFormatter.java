package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class CurrentFormatter {
    public static String format(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        String formatted = df.format(value) +" đ";
        return formatted;
    }

    public static double deformat(String formatted) {
        if (formatted == null || formatted.isEmpty()) {
            return 0;
        }
        String numeric = formatted.replace("đ", "").trim();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setGroupingUsed(true);
        try {
            Number number = df.parse(numeric);
            return number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
