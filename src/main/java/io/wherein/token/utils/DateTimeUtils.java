package io.wherein.token.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * DateTime Util.
 */
@Slf4j
public class DateTimeUtils {

  public static final String DATE_FORMAT_TIME = "HH:mm";
  public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
  public static final String DATE_FORMAT_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String SYNC_TIME = "08:05";
  public static final String TIME_REGEX = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$";

  /**
   * Compare two times by date format.
   *
   * @param sourceDateStr source date string.
   * @param targetDateStr target date string.
   * @param formatType date format type, can be customized. "yyyy-MM-DD" "yyyyMMdd HH:mm:ss".
   */
  public static boolean before(String sourceDateStr, String targetDateStr, String formatType)
      throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
    Date sourceDate = simpleDateFormat.parse(sourceDateStr);
    Date targetDate = simpleDateFormat.parse(targetDateStr);
    return sourceDate.before(targetDate);
  }

  /**
   * Get the date string by current time.
   */
  public static String getDate() {
    // Check if the current time is before 08:05am.
    SimpleDateFormat dateFormatTime = new SimpleDateFormat(DATE_FORMAT_TIME);
    Date nowDateTime = new Date();
    String nowTimeStr = dateFormatTime.format(nowDateTime);
    boolean isBefore = false;
    try {
      isBefore = before(nowTimeStr, SYNC_TIME, DATE_FORMAT_TIME);
    } catch (ParseException e) {
      log.error("Parse date error!", e);
    }

    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    // If current time is before 08:05am, it will get the data of the day before yesterday.
    if (isBefore) {
      calendar.add(Calendar.DATE, -2);
    }
    // If current time is not before 08:05am, it will get the data of yesterday.
    else {
      calendar.add(Calendar.DATE, -1);
    }
    return dateFormatDay.format(calendar.getTime());
  }

  /**
   * Validate whether the date string is standard.
   *
   * @param date date string, "2012-01-31".
   */
  public static boolean validateDate(String date) {
    // TODO validate the start time and end time.
    if (StringUtils.isBlank(date)) {
      return false;
    }
    return Pattern.matches(TIME_REGEX, date);
  }

  /**
   * get yesterday string.
   */
  public static String getYesterday() {
    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    return dateFormatDay.format(calendar.getTime());
  }
}
