package io.wherein.cnstm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.wherein.cnstm.mapper.TokenMapper;
import io.wherein.cnstm.service.TokenService;
import io.wherein.cnstm.utils.DateTimeUtils;
import io.wherein.cnstm.utils.HttpClientUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Token service implement.
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

  @Value("${url}")
  private String url;

  @Resource
  private TokenMapper tokenMapper;

  @Override
  public List<Map<String, Object>> getAll(String date) {
    List<Map<String, Object>> tokens = tokenMapper.getTokenByDate(date);
    List<Map<String, Object>> totalSP = tokenMapper.getTotalSP();
    Map<String, BigDecimal> sPs = new HashMap<>(128);
    for (Map<String, Object> sp : totalSP) {
      sPs.put(sp.get("steem_id").toString(), new BigDecimal(sp.get("totalSP").toString()));
    }

    for (Map<String, Object> token : tokens) {
      String steemId = token.get("steem_id").toString();
      if (sPs.containsKey(steemId)) {
        token.put("totalToken", new BigDecimal(sPs.get(steemId).toString()));
      }
    }
    return tokens;
  }

  /**
   * Get token by date.
   *
   * @return List.
   */
  @Override
  public List<Map<String, Object>> getTokenByDate(String date) {
    return tokenMapper.getTokenByDate(date);
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
    List<Map> sp = new ArrayList<>(1);
    try {
      String response = HttpClientUtils.doGet(url);
      sp = JSON.parseArray(response, Map.class);
    } catch (URISyntaxException | IOException e) {
      log.error("Http request error!", e);
    } catch (JSONException e) {
      log.error("Parse JSON error!", e);
    }
    return sp;
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
    if (list != null && !list.isEmpty()) {
      tokenMapper.addSP(list);
    }
  }

  /**
   * Sync from steem.
   */
  @Override
  public void syncFromSteem() {
    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    String date = dateFormatDay.format(calendar.getTime());
    int count = getCountByDate(date);
    if (count != 0) {
      log.info("There is already the data for today!");
      return;
    }
    List<Map> spFromSteem = getSPFromSteem();
    addSP(spFromSteem);
  }
}
