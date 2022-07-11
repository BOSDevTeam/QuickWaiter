package common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SystemSetting {
    public static final String SINGLE_CHECK="Single";
    public static final String MULTI_CHECK="Multi";
    private static final String TIME_FORMAT="hh:mm a";
    private Calendar cCalendar;

    public String getCurrentTime(){
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
        return timeFormat.format(cCalendar.getTime());
    }
}
