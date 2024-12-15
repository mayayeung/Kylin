package com.martin.core.utils;


import com.martin.core.R;
import com.martin.core.config.AppConfig;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 时间转换工具
 */
public class TimeUtil {


    private TimeUtil() {
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        if (calendar.before(inputTime)) {
            //今天23:59在输入时间之前，解决一些时间误差，把当天时间显示到这里
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + AppConfig.getContext().getResources()
                    .getString(R.string.time_year) + "MM" + AppConfig.getContext().getString(R.string
                    .time_month) + "dd" + AppConfig.getContext().getResources().getString(
                    R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return AppConfig.getContext().getResources().getString(R
                    .string
                    .time_yesterday);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + AppConfig.getContext().getResources()
                        .getString(R.string.time_month) + "d" + AppConfig.getContext().getResources()
                        .getString(R.string.time_day));
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + AppConfig.getContext().getResources().getString(R.string.time_year) + "MM" + AppConfig.getContext().getResources().getString(R.string.time_month) + "dd" + AppConfig.getContext().getResources().getString(R.string.time_day));
                return sdf.format(currenTimeZone);

            }

        }

    }

    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (!calendar.after(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + AppConfig.getContext()
                    .getResources().getString(R.string.time_year) + "MM" + AppConfig.getContext().getResources().getString(R.string.time_month) + "dd" + AppConfig.getContext().getResources().getString(R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return AppConfig.getContext().getResources().getString(R.string.time_yesterday) + " " + sdf.format(currenTimeZone);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + AppConfig.getContext().getResources().getString(R.string.time_month) + "d"/*+AppConfig.getContext().getResources().getString(R.string.time_day)+" HH:mm"*/);
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + AppConfig.getContext().getResources().getString(R.string.time_year) + "MM" + AppConfig.getContext().getResources().getString(R.string.time_month) + "dd"/*+AppConfig.getContext().getResources().getString(R.string.time_day)+" HH:mm"*/);
                return sdf.format(currenTimeZone);
            }

        }

    }

    public static String getVideoLimitStr(long maxLength){
        long m = maxLength/60;
        long s = maxLength%60;
        StringBuilder builder = new StringBuilder();
        if(m > 0){
            builder.append(m).append("分");
        }
        if(s > 0){
            builder.append(s).append("秒");
        } else{
            builder.append("钟");
        }
        return builder.toString();
    }

    /**
     * 获取分秒格式的时长 05:30
     * @param millSeconds
     * @return
     */
    public static String getMaxDuration(long millSeconds) {
        //这里position单位是毫秒
        String time = "00:00";
        if (millSeconds > 0) {
            long minute = millSeconds / 1000 % (24 * 60 * 60) % (60 * 60) / 60;
            long second = millSeconds / 1000 % (24 * 60 * 60) % (60 * 60) % 60;
            time = String.format("%02d:%02d", minute, second);
        }
        return time;
    }


    public static String getDurationStr(long ms) {
        int second = (int)(ms/1000);
        int minute = second/60;
        int hour = minute/60;
        if (ms < 1000) {
            return ms + "ms";
        } else if (hour > 0) {
            return String.format("%02d:%02d:%02d",hour,minute%60,second%60);
        } else {
            return String.format("%02d:%02d",minute,second%60);
        }
    }

    public static String getTime(long time) {
        Calendar newCalendar = Calendar.getInstance();
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTime(new Date(time));
        if (newCalendar.get(Calendar.YEAR) > oldCalendar.get(Calendar.YEAR)) {
            return getFormatTime(time);
        } else if (newCalendar.get(Calendar.MONTH) > oldCalendar.get(Calendar.MONTH)) {
            return (newCalendar.get(Calendar.MONTH) - oldCalendar.get(Calendar.MONTH)) + "月前";
        } else if (newCalendar.get(Calendar.DAY_OF_MONTH) > oldCalendar.get(Calendar.DAY_OF_MONTH)) {
            return (newCalendar.get(Calendar.DAY_OF_MONTH) - oldCalendar.get(Calendar.DAY_OF_MONTH)) + "天前";
        } else if (newCalendar.get(Calendar.HOUR) > oldCalendar.get(Calendar.HOUR)) {
            return (newCalendar.get(Calendar.HOUR) - oldCalendar.get(Calendar.HOUR)) + "小时前";
        } else if (newCalendar.get(Calendar.MINUTE) > oldCalendar.get(Calendar.MINUTE)) {
            return (newCalendar.get(Calendar.MINUTE) - oldCalendar.get(Calendar.MINUTE)) + "分钟前";
        } else {
            return "刚刚";
        }
    }

    public static String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(time));
    }
}
