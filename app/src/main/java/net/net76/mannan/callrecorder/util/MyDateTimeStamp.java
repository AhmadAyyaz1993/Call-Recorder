package net.net76.mannan.callrecorder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MANNAN on 4/29/2016.
 */
public class MyDateTimeStamp {

    public static String emptyTimeStamp = "0000-00-00 00:00:00";

    public static String getDateTimeStamp(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = formatter.format(c.getTime());

        return dateTimeString ;
    }

    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(c.getTime());

        return dateString ;
    }

    public static String getCurrentTimeForFile(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH.mm.ss");
        String timeString = formatter.format(c.getTime());

        return timeString ;
    }

    public static String getMonthName(String selectedMonth){
        String monthName = "";
        if (selectedMonth.contentEquals("1")) {monthName = "Jan";}
        else if (selectedMonth.contentEquals("2")){monthName = "Feb";}
        else if (selectedMonth.contentEquals("3")){monthName = "Mar";}
        else if (selectedMonth.contentEquals("4")){monthName = "April";}
        else if (selectedMonth.contentEquals("5")){monthName = "May";}
        else if (selectedMonth.contentEquals("6")){monthName = "June";}
        else if (selectedMonth.contentEquals("7")){monthName = "July";}
        else if (selectedMonth.contentEquals("8")){monthName = "Aug";}
        else if (selectedMonth.contentEquals("9")){monthName = "Sep";}
        else if (selectedMonth.contentEquals("10")){monthName = "Oct";}
        else if (selectedMonth.contentEquals("11")){monthName = "Nov";}
        else if (selectedMonth.contentEquals("12")){monthName = "Dec";}
        return monthName;
    }

    public static String getTime(String hour, String minute){

        String aTime = hour+':'+minute;
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = displayFormat.parse(aTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = aTime;
        try {
            time = "" + parseFormat.format(date);
        }catch (Exception e){
        }

        return time;
    }

    public static String timeDurationString(int hours, int minutes, int seconds){
        String timeDurationString = "N/A";
        if (hours == 0){
            timeDurationString = String.format("%2dm %2ds", minutes, seconds);
        }
        if (hours == 0 && seconds ==0){
            timeDurationString = String.format("%2dm", minutes);
        }
        if (minutes == 0 && minutes ==0){
            timeDurationString = String.format("%2ds", seconds);
        }
        if (seconds == 0){
            timeDurationString = String.format("%2dh %2dm", hours, minutes);
        }
        if (hours !=0 && minutes != 0 && seconds !=0){
            timeDurationString = String.format("%2dh %2dm %2ds", hours, minutes, seconds);
        }
        if (hours ==0 && minutes == 0 && seconds ==0){
            timeDurationString = String.format("%2ds",minutes);
        }

        return timeDurationString;
    }

    public static long dateTimeLong(String dateTimeString){

        Date dateStart = null;
        long dateTimeLong = 0;
        try {
            dateTimeLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeLong;
    }
}
