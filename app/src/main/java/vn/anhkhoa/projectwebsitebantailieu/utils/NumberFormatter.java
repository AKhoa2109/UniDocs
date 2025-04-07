package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.text.DecimalFormat;

public class NumberFormatter {
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static String formatterNum(Long number){
        String s = String.valueOf(number);
        int n = s.length();
        if(n<=3){
            return s;
        }

        else if(n<=6){
            return formatter.format(number) + "k";
        }
        else if (n <= 8) {
            long prefix = Long.parseLong(s.substring(0, s.length() - 3));
            return formatter.format(prefix) + "tr";
        }
        else {
            long prefix = Long.parseLong(s.substring(0, s.length() - 6));
            return formatter.format(prefix) + "tr";
        }
    }
}
