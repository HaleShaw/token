package io.wherein.token.utils;

/**
 * String utils.
 */
public class StringUtils {

  public final static String ACCOUNT_CNSTM = "cnstm";
  public final static String ACCOUNT_WHEREIN = "wherein";

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
}
