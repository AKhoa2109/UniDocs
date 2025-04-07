package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrentFormatter {
    public static String format(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        String formatted = df.format(value) +" đ";
        return formatted;
    }
}
