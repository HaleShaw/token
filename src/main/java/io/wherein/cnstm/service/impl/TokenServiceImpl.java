package io.wherein.cnstm.service.impl;

import com.alibaba.fastjson.JSON;
import io.wherein.cnstm.mapper.TokenMapper;
import io.wherein.cnstm.service.TokenService;
import io.wherein.cnstm.utils.HttpClientUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Token service implement.
 */
@Service
public class TokenServiceImpl implements TokenService {

  @Value("${url}")
  private String url;

  @Resource
  private TokenMapper tokenMapper;

  @Override
  public List<Map<String, Object>> getAll(String date) {
    List<Map<String, Object>> currentToken = tokenMapper.getCurrentToken(date);
    List<Map<String, Object>> totalSP = tokenMapper.getTotalSP();
    Map<String, BigDecimal> sPs = new HashMap<>(128);
    for (Map<String, Object> sp : totalSP) {
      sPs.put(sp.get("steem_id").toString(), new BigDecimal(sp.get("totalSP").toString()));
    }

    for (Map<String, Object> token : currentToken) {
      String steemId = token.get("steem_id").toString();
      if (sPs.containsKey(steemId)) {
        token.put("totalToken", new BigDecimal(sPs.get(steemId).toString()));
      }
    }
    return currentToken;
  }

  /**
   * Get token by date.
   *
   * @return List.
   */
  @Override
  public List<Map<String, Object>> getCurrentToken(String date) {
    return tokenMapper.getCurrentToken(date);
  }

  /**
   * Get total sp for each account.
   *
   * @return List.
   */
  @Override
  public List<Map<String, Object>> getTotalSP() {
    return tokenMapper.getTotalSP();
  }

  /**
   * Get sp from steem of current day.
   *
   * @return List.
   */
  @Override
  public List<Map> getSPFromSteem() {
    String response = null;
    try {
      response = HttpClientUtils.doGet(url);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO Exception
    List<Map> res = JSON.parseArray(response, Map.class);
    return res;
  }

  /**
   * Get count by date.
   *
   * @param date date.
   * @return count of data.
   */
  @Override
  public int getCountByDate(String date) {
    return tokenMapper.getCountByDate(date);
  }

  @Override
  public void addSP(List list) {
    tokenMapper.addSP(list);
  }
}
