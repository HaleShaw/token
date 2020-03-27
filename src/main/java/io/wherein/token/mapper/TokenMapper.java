package io.wherein.token.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Token Mapper.
 */
public interface TokenMapper {

  /**
   * Get token by date.
   *
   * @param account account.
   * @param date date.
   * @return List.
   */
  List<Map<String, Object>> getTokenByDate(@Param(value = "account") String account,
      @Param(value = "date") String date);

  /**
   * Insert sp into DB.
   *
   * @param account account.
   * @param list sp of the list.
   * @return the count of values.
   */
  int insertSp(String account, List list);

  /**
   * Get count by date.
   *
   * @param account account.
   * @param date date.
   * @return count of data.
   */
  int getCountByDate(@Param(value = "account") String account, @Param(value = "date") String date);

  /**
   * Get last date.
   *
   * @param account account.
   * @return the last date of the data.
   */
  String getLastDate(@Param(value = "account") String account);
}
