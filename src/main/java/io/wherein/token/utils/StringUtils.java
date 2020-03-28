package io.wherein.token.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * String utils.
 */
public class StringUtils {

  public final static String ACCOUNT_CNSTM = "cnstm";
  public final static String ACCOUNT_WHEREIN = "wherein";

  public final static String MAIL_SUBJECT = "[System mail] Sync SP from Steemit failed";
  private final static String MAIL_CONTENT = "Hi,\nSynchronize data failed!\n\nAccount: %s\nData of date: %s\nSync time: %s\n\nError log:\n%s\n\nSystem mail, please do not reply.";

  /**
   * Validate whether the account is available.
   *
   * @param account account.
   */
  public static boolean accountIsAvailable(String account) {
    if (org.apache.commons.lang3.StringUtils.isBlank(account) || (
        !ACCOUNT_CNSTM.equalsIgnoreCase(account)
            && !ACCOUNT_WHEREIN.equalsIgnoreCase(account))) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * get mail content.
   *
   * @param account account.
   * @param date date.
   * @param time time.
   * @param e exception.
   * @return mail content.
   */
  public static String getMailContent(String account, String date, String time, Exception e) {
    return String.format(MAIL_CONTENT, account, date, time, ExceptionUtils.getStackTrace(e));
  }
}
