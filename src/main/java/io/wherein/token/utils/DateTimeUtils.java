package io.wherein.token.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * DateTime Util.
 */
@Slf4j
public class DateTimeUtils {

  public static final String DATE_FORMAT_TIME = "HH:mm";
  public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
  public static final String DATE_FORMAT_DAY_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String SYNC_TIME = "08:05";

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
}
