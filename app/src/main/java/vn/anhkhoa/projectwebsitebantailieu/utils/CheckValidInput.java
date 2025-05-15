package vn.anhkhoa.projectwebsitebantailieu.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import com.google.android.material.textfield.TextInputLayout;

public class CheckValidInput {
    public static boolean checkEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
    public static boolean checkPassword(String password) {
        boolean check = true;
        if (password.length() < 6) {
            check = false;
        }
        return check;
    }
    public static boolean checkConfirmPassword(String password, String confirmPassword) {
        boolean check = true;
        if (!password.equals(confirmPassword)) {
            check = false;
        }
        return check;
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        String regex = "^(03|05|07|08|09)\\d{8}$";
        return phoneNumber.matches(regex);
    }

    public static boolean checkAge(String birthDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate birth = LocalDate.parse(birthDate, formatter);
            LocalDate today = LocalDate.now();
            Period period = Period.between(birth, today);
            return period.getYears() >= 18;
        } catch (Exception e) {
            return false;
        }
    }

    
}
