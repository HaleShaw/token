package io.wherein.cnstm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateTime Util.
 */
public class DateTimeUtils {

  public static final String DATE_FORMAT_TIME = "HH:mm";
  public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
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
}
