package io.wherein.token.utils;

/**
 * String utils.
 */
public class StringUtils {

  public final static String ACCOUNT_CNSTM = "cnstm";
  public final static String ACCOUNT_WHEREIN = "wherein";

  public final static String MAIL_SUBJECT = "[System mail] Sync SP from Steemit failed";

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
    return "Hi,\nFailed to synchronize data of " + account + " of " + date + " from Steemit at "
        + time + ".\nFollowing is the error log.\n\n" + e.toString()
        + "\n\nSystem mail, please do not reply.";
  }
}
