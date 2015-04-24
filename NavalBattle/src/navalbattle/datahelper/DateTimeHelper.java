package navalbattle.datahelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {

    public static class DateHour {

        private final String date;
        private final String hour;

        public DateHour(String date, String hour) {
            this.date = date;
            this.hour = hour;
        }

        public String getDate() {
            return date;
        }

        public String getHour() {
            return hour;
        }
    }

    public static boolean isHourValid(String in) {
        try {
            if (in.length() == 5 && in.charAt(2) == ':') {
                String hourStr = in.substring(0, 2);
                String minuteStr = in.substring(3, 5);

                Integer hour = Integer.parseInt(hourStr);
                Integer minute = Integer.parseInt(minuteStr);

                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    throw new NumberFormatException();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

     public static String dateToUniversalOnlyDateString(Date dt) {
        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd");
        return tf.format(dt);
    }
    
    public static String dateToUniversalDateString(Date dt) {
        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return tf.format(dt);
    }

    public static Date dateFromUniversalDateString(String in) {
        if (!DateTimeHelper.isUniversalDateStringValid(in)) {
            throw new IllegalArgumentException("Invalid datetime supplied");
        }

        SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            return tf.parse(in);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Invalid datetime supplied");
        }
    }

    public static boolean isUniversalDateStringValid(String in) {
        DateTimeHelper.DateHour dateHour = DateTimeHelper.extractDateHour(in);

        return DateTimeHelper.isDateValid(dateHour.getDate()) && DateTimeHelper.isHourValid(dateHour.getHour());
    }

    public static boolean isDateValid(String in) {

        try {
            if (in.length() == 10 && in.charAt(4) == '-' && in.charAt(7) == '-') {
                String yearStr = in.substring(0, 4);
                String monthStr = in.substring(5, 7);
                String dayStr = in.substring(8, 10);

                Integer year = Integer.parseInt(yearStr);
                Integer month = Integer.parseInt(monthStr);
                Integer day = Integer.parseInt(dayStr);

                if (year < 0 || month < 1 || month > 12 || day < 1 || day > 31) {
                    throw new NumberFormatException();
                }

                Calendar calendar = new GregorianCalendar(year, month, day);
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (day > daysInMonth) {
                    throw new Exception();
                }

            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    // 2014-01-02 12:14
    public static DateHour extractDateHour(String in) {
        final int len = in.length();

        if (len == 14 || len == 16) {
            String date = in.substring(0, 10);
            String hour = in.substring(11);

            return new DateTimeHelper.DateHour(date, hour);
        } else {
            throw new IllegalArgumentException("Invalid length");
        }
    }

    public static Date addMinutesToDate(Date in, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(in);
        calendar.add(Calendar.MINUTE, amount);
        return calendar.getTime();
    }

    public static long deltaTwoDates(Date d1, Date d2, TimeUnit tu) {
        return tu.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
    }
}
