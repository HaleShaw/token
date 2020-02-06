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
  List<Map> getSPFromSteem(String account) throws IOException, URISyntaxException;

  /**
   * Get count by date.
   *
   * @param account account.
   * @param date date.
   * @return count of data.
   */
  int getCountByDate(String account, String date);

  void addSP(List list);

  /**
   * Sync from steem.
   *
   * @param account account.
   */
  void syncFromSteem(String account);
}
