package az.afb.mkrvalidator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValidationUtil {


    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}