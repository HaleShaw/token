package io.wherein.cnstm.service;

import java.util.List;
import java.util.Map;

/**
 * Token Service.
 */
public interface TokenService {

  List<Map<String, Object>> getAll(String date);

  /**
   * Get token by date.
   *
   * @return List.
   */
  List<Map<String, Object>> getTokenByDate(String date);

  /**
   * Get total sp for each account.
   *
   * @return List.
   */
  List<Map<String, Object>> getTotalSP();

  /**
   * Get sp from steem of current day.
   *
   * @return List.
   */
  List<Map> getSPFromSteem();

  /**
   * Get count by date.
   *
   * @param date date.
   * @return count of data.
   */
  int getCountByDate(String date);

  void addSP(List list);

  /**
   * Sync from steem.
   */
  void syncFromSteem();
}
