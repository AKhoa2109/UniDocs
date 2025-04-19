package vn.anhkhoa.projectwebsitebantailieu.utils;

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

}
