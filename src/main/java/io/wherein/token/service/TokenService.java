package io.wherein.token.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Token Service.
 */
public interface TokenService {

  /**
   * Get token by date.
   *
   * @return List.
   */
  List<Map<String, Object>> getTokenByDate(String account, String date);

  /**
   * Get sp from steem of current day.
   *
   * @param account account.
   * @return List.
   */
  List<Map> getSpFromSteem(String account) throws IOException, URISyntaxException;

  /**
   * Get count by date.
   *
   * @param account account.
   * @param date date.
   * @return count of data.
   */
  int getCountByDate(String account, String date);

  /**
   * Insert sp into DB.
   *
   * @param account account.
   * @param list sp of the list.
   */
  void insertSp(String account, List list);

  /**
   * Sync from steem.
   *
   * @param account account.
   */
  void syncFromSteem(String account) throws IOException, URISyntaxException;

  /**
   * Sync from steem. If it failed, it will retry.
   *
   * @param account account.
   */
  void syncFromSteemRetry(String account) throws IOException, URISyntaxException;
}
