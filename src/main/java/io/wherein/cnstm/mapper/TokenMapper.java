package io.wherein.cnstm.mapper;

import io.wherein.cnstm.model.Token;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * Token Mapper.
 */
public interface TokenMapper {

  List<Token> getAll();

  /**
   * Get token by date.
   *
   * @param date date.
   * @return List.
   */
  List<Map<String, Object>> getTokenByDate(@Param(value = "date") String date);

  /**
   * Get total sps for each account.
   *
   * @return List.
   */
  List<Map<String, Object>> getTotalSP();

  int addSP(List list);

  /**
   * Get count by date.
   *
   * @param date date.
   * @return count of data.
   */
  int getCountByDate(@Param(value = "date") String date);

  /**
   * Get last date.
   */
  String getLastDate();
}
