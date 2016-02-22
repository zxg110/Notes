package com.zxg.notes.util;
/**
 * 日期处理工具类
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zxg.notes.R;

import android.content.Context;

public class DateUtil {

    private static Map<String, SimpleDateFormat> mSimpleDateFormatMap = new HashMap<String, SimpleDateFormat>();

    public static String getStandardTime(String format, long timestamp) {
        SimpleDateFormat sdf = mSimpleDateFormatMap.get(format);
        if (null == sdf) {
            sdf = new SimpleDateFormat(format);
            mSimpleDateFormatMap.put(format, sdf);
        }
        Date date = new Date(timestamp);
        sdf.format(date);
        return sdf.format(date);
    }

    public static String converTime(Context context, long timestamp) {
        long currentSeconds = System.currentTimeMillis() / 1000;
        long timeGap = currentSeconds - timestamp / 1000;
        String timeStr = null;
        if (timeGap > 24 * 60 * 60 * 365) {
            // one year ago format:2014-05-23
            timeStr = getStandardTime("yy/MM/dd", timestamp);
        } else if (timeGap > 24 * 60 * 60) {
            // one day ago format: 08-14 09:23
            timeStr = getStandardTime("MM/dd HH:mm", timestamp);
        } else if (timeGap > 60 * 60) {
            // 1 day-24 hours
            // if(timeGap / (60*60) >1){
            timeStr = timeGap / (60 * 60) + " "
                    + context.getString(R.string.time_hour_ago);
            // }
            // else{
            // timeStr = timeGap / (60 * 60)+" "
            // +context.getString(R.string.time_hour_ago);
            // }
        } else if (timeGap > 60) {
            timeStr = timeGap / 60 + " "
                    + context.getString(R.string.time_minute_ago);
        } else {
            // 1 minutes-59 seconds
            timeStr = context.getString(R.string.time_rightnow);
        }
        return timeStr;
    }
}
