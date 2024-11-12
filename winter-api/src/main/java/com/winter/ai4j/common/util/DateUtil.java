package com.winter.ai4j.common.util;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作工具类
 */
public class DateUtil {

    // TODO 这个工具类有bug，需要进行优化

    private static final String dateFormat = "yyyy-MM-dd";


    /**
     * 获取两个时间差 单位：秒
     * @param date1
     * @param date2
     * @return
     */
    public Long getTimeSubtract(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / 1000;
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);

    }

    /**
     * 截取比较断两个日期对象的field处的值 。
     * 如果第一个日期小于、等于、大于第二个，则对应返回负整数、0、正整数
     *
     * @param date1 第一个日期对象，非null
     * @param date2 第二个日期对象，非null
     * @param field Calendar中的阈值
     *              <p>
     *              date1 > date2  返回：1
     *              date1 = date2  返回：0
     *              date1 < date2  返回：-1
     */
    public static int truncatedCompareTo(final Date date1, final Date date2, final int field) {
        return DateUtil.truncatedCompareTo(date1, date2, field);
    }

    /**
     * 比对时间大小
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static boolean dateCompare(Date beginDate, Date endDate) {
        // beginDate > endDate
        if (DateUtil.truncatedCompareTo(beginDate, endDate, Calendar.SECOND) == 1) {
            return false;
        }
        // beginDate  <= endDate
        return true;
    }

    /**
     * @param nowDate
     * @param days
     * @return: java.util.Date
     * 功能描述: 给时间增加一个天数
     */
    public static Date datePlus(Date nowDate, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);

        calendar.add(Calendar.DATE,days);
        return calendar.getTime();
    }

    /**
     * @param nowDate
     * @param hours
     * @return: java.util.Date
     * 功能描述: 给时间增加一个指定的 小时
     */
    public static Date datePlusHours(Date nowDate, int hours) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);

        calendar.add(Calendar.HOUR,hours);
        return calendar.getTime();
    }

    /**
     * @param date
     * @return: java.lang.String
     * 功能描述: 获取对应的时间格式
     */
    public static String getYyyymmddhhmmss(Date date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
